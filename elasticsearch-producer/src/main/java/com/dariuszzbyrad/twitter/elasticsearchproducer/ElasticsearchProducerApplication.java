package com.dariuszzbyrad.twitter.elasticsearchproducer;

import com.dariuszzbyrad.twitter.elasticsearchproducer.job.ElasticTweetProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class ElasticsearchProducerApplication {

    private static void main(final String[] args) {
        ApplicationContext context = SpringApplication.run(ElasticsearchProducerApplication.class, args);

        ElasticTweetProducer elasticTweetProducer = context.getBean(ElasticTweetProducer.class);

        TaskExecutor taskExecutor = context.getBean(TaskExecutor.class);
        taskExecutor.execute(elasticTweetProducer);
    }
}
