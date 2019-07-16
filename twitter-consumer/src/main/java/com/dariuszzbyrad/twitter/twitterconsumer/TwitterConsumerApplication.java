package com.dariuszzbyrad.twitter.twitterconsumer;

import com.dariuszzbyrad.twitter.twitterconsumer.job.TwitterConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class TwitterConsumerApplication {

    public static void main(final String[] args) {
        ApplicationContext context = SpringApplication.run(TwitterConsumerApplication.class, args);

        TwitterConsumer twitterProducer = context.getBean(TwitterConsumer.class);

        TaskExecutor taskExecutor = context.getBean(TaskExecutor.class);
        taskExecutor.execute(twitterProducer);
    }
}
