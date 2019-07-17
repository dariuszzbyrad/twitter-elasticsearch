# Kafka + Twitter API example

The repository is something like testing ground for testing Twitter API, Kafka, ElasticSearch and Kibana. 



# Design 

The Twitter producer is also Kafka consumer for fetching new tweets and send to Kafka. The Kafka consumer send tweets to Elastic Search. In the end, the Kibana will analyze and show tweet statistics.


![alt text][logo]

[logo]: img/diagram.png "Diagram"



# Run

1. **Run Kafka and Elasticsearch**

   ```bash
   $ cd docker 
   $ docker-compose -f ecosystem.yml up -d
   
   Starting elasticsearch ... done
   Starting docker_zoo1_1 ... done
   Starting docker_kafka3_1 ... done
   Starting docker_kafka2_1 ... done
   Starting docker_kafka1_1 ... done
   ```

2. **Run Twitter Consumer project** 

   manually

   ```bash
   $ cd twitter-consumer
   $ mvn spring-boot:run
   ```

   or by docker

   ```bash
   $ cd twitter-consumer
   $ mvn verify jib:dockerBuild 
   $ docker run twitter-consumer:latest
   ```

   

3. **Run Elasticsearch Producer project** 

   manually

   ```bash
   $ cd elasticsearch-producer 
   $ mvn spring-boot:run
   ```

   or by docker

   ```bash
   $ cd elasticsearch-producer 
   $ mvn verify jib:dockerBuild 
   $ docker run elasticsearch-producer:latest
   ```

4. **Verify**

   ```bash
   $ docker ps
   CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                                            NAMES
   db71ff81826b        elasticsearch-producer:latest                         "sh -c 'chmod +x /en…"   17 minutes ago      Up 17 minutes       8080/tcp                                         angry_ramanujan
   c5e7848c30f9        twitter-consumer:latest                               "sh -c 'chmod +x /en…"   20 minutes ago      Up 20 minutes       8080/tcp                                         suspicious_khorana
   744f532f4355        docker.elastic.co/elasticsearch/elasticsearch:6.6.0   "/usr/local/bin/dock…"   4 days ago          Up About a minute   0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch
   c5902ec86a01        confluentinc/cp-kafka:5.2.1                           "/etc/confluent/dock…"   2 weeks ago         Up About a minute   9092/tcp, 0.0.0.0:9093->9093/tcp                 docker_kafka2_1
   0f203cf440b9        confluentinc/cp-kafka:5.2.1                           "/etc/confluent/dock…"   2 weeks ago         Up About a minute   0.0.0.0:9092->9092/tcp                           docker_kafka1_1
   9c8710ada420        confluentinc/cp-kafka:5.2.1                           "/etc/confluent/dock…"   2 weeks ago         Up About a minute   9092/tcp, 0.0.0.0:9094->9094/tcp                 docker_kafka3_1
   4afa6a2a23fd        zookeeper:3.4.9                                       "/docker-entrypoint.…"   2 weeks ago         Up About a minute   2888/tcp, 0.0.0.0:2181->2181/tcp, 3888/tcp       docker_zoo1_1
   ```