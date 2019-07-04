package com.dariuszzbyrad.twitter.twitterconsumer.config;

import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwitterAuthentication {

    private final String consumerAPIKey;
    private final String consumerSecretKey;
    private final String accessToken;
    private final String accessTokenSecret;

    public TwitterAuthentication(@Value("${consumer.api.key}") String consumerAPIKey,
                                 @Value("${consumer.secret.key}") String consumerSecretKey,
                                 @Value("${access.token}") String accessToken,
                                 @Value("${access.token.secret}")String accessTokenSecret) {

        this.consumerAPIKey = consumerAPIKey;
        this.consumerSecretKey = consumerSecretKey;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    public Authentication getAuthentication() {
        return new OAuth1(consumerAPIKey, consumerSecretKey, accessToken, accessTokenSecret);
    }
}
