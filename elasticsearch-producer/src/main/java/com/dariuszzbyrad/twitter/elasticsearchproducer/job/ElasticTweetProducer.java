package com.dariuszzbyrad.twitter.elasticsearchproducer.job;

import com.dariuszzbyrad.twitter.elasticsearchproducer.config.KafkaConsumerConfig;
import com.dariuszzbyrad.twitter.elasticsearchproducer.elasticsearch.ElasticsearchSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@Scope("prototype")
public class ElasticTweetProducer implements Runnable {

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    @Autowired
    private ElasticsearchSender elasticsearchSender;

    private static final int ONE_SECOND_IN_MILLIS = 1000;
    private static final int ONE_HUNDRED_MILLIS = 100;

    /**
     * Run job for fetching new tweets from Kafka and send to Elasticsearch.
     */
    public void run() {
        KafkaConsumer<String, String> consumer = kafkaConsumerConfig.getConsumer();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(ONE_HUNDRED_MILLIS));

            for (ConsumerRecord<String, String> record : records) {
                log.debug("Fetched record from MQ. Key: {}, Value: {}", record.key(), record.value());

                String msgId = elasticsearchSender.sendToElasticsearch(record.value());
                log.debug("Tweet has been send to Elasticsearch. Message Id: {}", msgId);

                waitOneSecond();
            }
        }
    }

    private void waitOneSecond() {
        try {
            Thread.sleep(ONE_SECOND_IN_MILLIS);
        } catch (InterruptedException e) {
            log.warn("The time break was interrupted.", e);
        }
    }
}
