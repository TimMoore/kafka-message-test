# Kafka message test



Download and extract http://apache.rediris.es/kafka/0.10.2.0/kafka_2.11-0.10.2.0.tgz


Terminal 1:
```
bin/zookeeper-server-start.sh config/zookeeper.properties

```

Terminal 2:

```
bin/kafka-server-start.sh config/server.properties
```

Terminal 3:


```
## since I only run one broker replication factor must be 1
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 40 --topic messages-alpha
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 40 --topic messages-beta
```

Terminal 4:

```
sbt runAll
```

Terminal 5:

```
curl -X POST http://localhost:9000/api/generate?count=100000
curl -X POST http://localhost:9000/api/consume-alpha
curl -X POST http://localhost:9000/api/consume-beta
```


```
bin/kafka-topics.sh --zookeeper localhost:2181  --list
bin/kafka-topics.sh  --zookeeper localhost:2181 --topic messages-alpha --describe
bin/kafka-topics.sh  --zookeeper localhost:2181 --topic messages-beta --describe
```
