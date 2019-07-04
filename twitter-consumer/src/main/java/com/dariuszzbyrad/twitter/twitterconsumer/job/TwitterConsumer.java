package com.dariuszzbyrad.twitter.twitterconsumer.job;

import com.dariuszzbyrad.twitter.twitterconsumer.config.KafkaConfig;
import com.dariuszzbyrad.twitter.twitterconsumer.config.TwitterAuthentication;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TwitterConsumer extends Thread {

    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${kafka.client.name}")
    private String clientName;

    @Value("${twitter.terms}")
    private String twitterTerms;

    private TwitterAuthentication twitterAuthentication;
    private KafkaConfig kafkaConfig;

    public void run()  {
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(10000);

        Client hosebirdClient = createTwitterClient(msgQueue);
        hosebirdClient.connect();

        KafkaProducer<String, String> producer = kafkaConfig.getKafkaProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            hosebirdClient.stop();
            producer.close();
        }));

        while (!hosebirdClient.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(msg);
            producer.send(new ProducerRecord<>(topicName, null, msg), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        log.error("Something bad happened", e);
                    }
                }
            });
        }

        hosebirdClient.stop();
    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) {

        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = getTwitterTerms();
        hosebirdEndpoint.trackTerms(terms);

        Authentication hosebirdAuth = twitterAuthentication.getAuthentication();

        ClientBuilder builder = new ClientBuilder()
                .name(clientName)                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));                        // optional: use this if you want to process client events

        return builder.build();
    }

    private List<String> getTwitterTerms() {
        return Arrays.asList(twitterTerms.split(","))
                .stream()
                .map(term -> term.trim())
                .collect(Collectors.toList());
    }
}
