package com.dariuszzbyrad.twitter.elasticsearchproducer.job;

import com.dariuszzbyrad.twitter.elasticsearchproducer.config.ElasticsearchConfig;
import com.dariuszzbyrad.twitter.elasticsearchproducer.config.KafkaConsumerConfig;
import com.dariuszzbyrad.twitter.elasticsearchproducer.elasticsearch.ElasticsearchSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@Scope("prototype")
public class ElasticTweetProducer implements Runnable {

    @Autowired
    KafkaConsumerConfig kafkaConsumerConfig;

    @Autowired
    ElasticsearchSender elasticsearchSender;

    public void run() {
        KafkaConsumer<String, String> consumer = kafkaConsumerConfig.getConsumer();

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn("The time break was interrupted.", e);
        }
    }
}