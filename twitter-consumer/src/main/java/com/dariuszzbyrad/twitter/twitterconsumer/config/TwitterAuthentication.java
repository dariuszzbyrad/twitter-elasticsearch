package com.dariuszzbyrad.twitter.twitterconsumer.config;

import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwitterAuthentication {

    @Value("${consumer.api.key}")
    private String consumerAPIKey;

    @Value("${consumer.secret.key}")
    private String consumerSecretKey;

    @Value("${access.token}")
    private String accessToken;

    @Value("${access.token.secret}")
    private String accessTokenSecret;

    /**
     * Get Authentication object for Twitter API.
     *
     * @return Authentication object.
     */
    public Authentication getAuthentication() {
        return new OAuth1(consumerAPIKey, consumerSecretKey, accessToken, accessTokenSecret);
    }
}
