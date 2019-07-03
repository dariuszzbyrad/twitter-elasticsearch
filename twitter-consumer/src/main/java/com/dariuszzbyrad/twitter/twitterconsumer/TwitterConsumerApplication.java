package com.dariuszzbyrad.twitter.twitterconsumer;

import com.dariuszzbyrad.twitter.twitterconsumer.job.TwitterConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TwitterConsumerApplication {

    private static ApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(TwitterConsumerApplication.class, args);

        TwitterConsumer twitterProducer = context.getBean(TwitterConsumer.class);

        twitterProducer.start();
    }

}
