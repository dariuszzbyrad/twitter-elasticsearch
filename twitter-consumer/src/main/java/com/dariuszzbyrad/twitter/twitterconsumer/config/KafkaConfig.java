package com.dariuszzbyrad.twitter.twitterconsumer.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class KafkaConfig {

    @Value("${kafka.bootstrap.server}")
    private String bootstrapServer;

    public static final int BATCH_SIZE_IN_BYTES = 32 * 1024;

    /**
     * Create kafka producer with default configuration.
     *
     * @return Kafka producer.
     */
    public KafkaProducer<String, String> getKafkaProducer() {
        Properties properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        /*
            Safe producer. Be careful, it might impact throughput and latency, always test for your use case
         */
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        //Ensure dta is properly replicated before and ack is received
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        //Ensure transient errors are retried indefinitely
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        /*
            Message Compression:
             - Much smaller producer size (compression ratio uo to 4x)
             - Faster to transfer data over the network.
             - Better throughput.
             - Better disk utilisation in Kafka.
         */
        ///Number of milliseconds a producer is willing to wait before sending a batch out. (default 0)
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        // Maximum number of bytes that will be included in a batch. (default 16KB)
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(BATCH_SIZE_IN_BYTES));
        //Compression type. (default none)
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return new KafkaProducer<>(properties);
    }

}
