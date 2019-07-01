# Kafka + Twitter API example

The repository is something like testing ground for testing Twitter API, Kafka, ElasticSearch and Kibana. 

# Design 

The Twitter producer is also Kafka consumer for fetching new tweets and send to Kafka. The Kafka consumer send tweets to Elastic Search. In the end, the Kibana will analyze and show tweet statistics.


![alt text][logo]

[logo]: img/diagram.png "Diagram"
