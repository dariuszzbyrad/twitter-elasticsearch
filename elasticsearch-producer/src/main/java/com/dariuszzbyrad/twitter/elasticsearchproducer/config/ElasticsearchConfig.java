package com.dariuszzbyrad.twitter.elasticsearchproducer.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchConfig {

    @Value("${elasticsearch.hostname}")
    private String hostname;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.protocol}")
    private String protocol;

    /**
     * Create and get Rest Client for Elasticsearch.
     *
     * @return The Rest Client.
     */
    public RestHighLevelClient getClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, protocol));

        return new RestHighLevelClient(builder);
    }
}
