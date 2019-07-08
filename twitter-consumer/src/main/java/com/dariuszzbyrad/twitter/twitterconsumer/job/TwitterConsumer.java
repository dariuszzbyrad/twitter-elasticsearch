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
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class TwitterConsumer implements Runnable {

    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${kafka.client.name}")
    private String clientName;

    @Value("${twitter.terms}")
    private String twitterTerms;

    private final TwitterAuthentication twitterAuthentication;
    private final KafkaConfig kafkaConfig;

    @Autowired
    public TwitterConsumer(TwitterAuthentication twitterAuthentication, KafkaConfig kafkaConfig) {
        this.twitterAuthentication = twitterAuthentication;
        this.kafkaConfig = kafkaConfig;
    }

    public void run() {
        log.info("Run twitter consumer job");
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(10000);
        Client twitterClient = createTwitterClient(msgQueue);
        twitterClient.connect();

        KafkaProducer<String, String> mqProducer = kafkaConfig.getKafkaProducer();

        registerActionsForProgramTermination(twitterClient, mqProducer);

        while (!twitterClient.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.take();
            } catch (InterruptedException e) {
                log.warn("Something went wrong", e);
            }
            log.debug("Received tweet: " + msg);
            sendTweetToMQ(mqProducer, msg);
        }

        twitterClient.stop();
    }

    /**
     * Send tweet to message queue.
     *
     * @param mqProducer Message Queue producer.
     * @param msg Message to send.
     */
    private void sendTweetToMQ(KafkaProducer<String, String> mqProducer, String msg) {
        mqProducer.send(new ProducerRecord<>(topicName, null, msg), (recordMetadata, e) -> {
            if (e != null) {
                log.error("Something bad happened", e);
            }
        });
    }

    /**
     * Close connection for Twitter and Kafka to be performed on a Program's termination.
     *
     * @param twitterClient The twitter client.
     * @param mqProducer The Message Queue producer.
     */
    private void registerActionsForProgramTermination(Client twitterClient, KafkaProducer<String, String> mqProducer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            twitterClient.stop();
            mqProducer.close();
        }));
    }

    private Client createTwitterClient(BlockingQueue<String> msgQueue) {
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = getTwitterTerms();
        hosebirdEndpoint.trackTerms(terms);

        Authentication hosebirdAuth = twitterAuthentication.getAuthentication();

        ClientBuilder builder = new ClientBuilder()
                .name(clientName)   // Not necessary, mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }

    private List<String> getTwitterTerms() {
        return Arrays.stream(twitterTerms.split(","))
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }
}
