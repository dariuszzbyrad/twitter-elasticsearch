package com.dariuszzbyrad.twitter.elasticsearchproducer.elasticsearch;

import com.dariuszzbyrad.twitter.elasticsearchproducer.config.ElasticsearchConfig;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ElasticsearchSender {

    @Value("${elasticsearch.index}")
    private String elasticsearchIndex;

    @Autowired
    private ElasticsearchConfig elasticsearchConfig;

    /**
     * Send message(record) to elasticsearch.
     *
     * @param record Record to send.
     * @return Record Id.
     */
    public String sendToElasticsearch(final String record) {
        String recordId = null;

        IndexRequest indexRequest = new IndexRequest(elasticsearchIndex).source(record, XContentType.JSON);

        try (RestHighLevelClient client = elasticsearchConfig.getClient()) {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            recordId = indexResponse.getId();
            log.info("Index request id {}", recordId);
        } catch (IOException e) {
            log.error("Something was wrong", e);
        }

        return recordId;
    }
}
