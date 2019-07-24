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
   
   Starting docker_zoo1_1 ... done
   Starting elasticsearch   ... done
   Starting docker_kafka1_1 ... done
   Starting docker_kafka2_1 ... done
   Starting docker_kafka3_1 ... done
   Starting kibana          ... done
   ```

2. **Configuration Twitter Consumer project** 

   Remove 'sample' extension for the application.properties.sample file and update credentials for Twitter API. 

3. **Run Twitter Consumer project** 

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

   

4. **Run Elasticsearch Producer project** 

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

5. **Verify**

   ```bash
   $ docker ps
   CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                                            NAMES
   db71ff81826b        elasticsearch-producer:latest                         "sh -c 'chmod +x /en…"   17 minutes ago      Up 17 minutes       8080/tcp                                         angry_ramanujan
   c5e7848c30f9        twitter-consumer:latest                               "sh -c 'chmod +x /en…"   20 minutes ago      Up 20 minutes       8080/tcp                                         suspicious_khorana
   7e4c5dab4c56        docker.elastic.co/kibana/kibana:7.2.0                 "/usr/local/bin/kiba…"   22 minutes ago      Up 44 seconds       0.0.0.0:5601->5601/tcp                           kibana
   0c96af136e91        docker.elastic.co/elasticsearch/elasticsearch:7.2.0   "/usr/local/bin/dock…"   3 hours ago         Up 47 seconds       0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch
   0d717cabc755        confluentinc/cp-kafka:5.2.1                           "/etc/confluent/dock…"   3 hours ago         Up 44 seconds       0.0.0.0:9092->9092/tcp                           docker_kafka1_1
   f4bbd4f167cc        confluentinc/cp-kafka:5.2.1                           "/etc/confluent/dock…"   3 hours ago         Up 45 seconds       9092/tcp, 0.0.0.0:9093->9093/tcp                 docker_kafka2_1
   74325cc19389        confluentinc/cp-kafka:5.2.1                           "/etc/confluent/dock…"   3 hours ago         Up 44 seconds       9092/tcp, 0.0.0.0:9094->9094/tcp                 docker_kafka3_1
   083778d693ba        zookeeper:3.4.9                                       "/docker-entrypoint.…"   3 hours ago         Up 47 seconds       2888/tcp, 0.0.0.0:2181->2181/tcp, 3888/tcp       docker_zoo1_1
   ```

6. **Check Kibana**

   http://localhost:5601/

   ![](img\kibana.png)


# Notices

I know, we have Kafka Connectors and my solution is like 'reinvent the wheel', but please remember that is education example to learn Kafka.