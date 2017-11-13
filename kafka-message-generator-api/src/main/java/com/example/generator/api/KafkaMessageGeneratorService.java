package com.example.generator.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface KafkaMessageGeneratorService extends Service {
    /**
     * Example: curl -X POST http://localhost:9000/api/generate?count=10000
     *
     * @param count the number of messages to generate to the topic
     */
    ServiceCall<NotUsed, Done> generate(int count);

    /**
     * This gets published to Kafka.
     */
    Topic<Message> messages();

    @Override
    default Descriptor descriptor() {
        return named("kafka-message-generator").withCalls(
                restCall(Method.POST, "/api/generate?count", this::generate)
        ).withTopics(
                topic("messages", this::messages)
                        // Kafka partitions messages, messages within the same partition will
                        // be delivered in order, to ensure that all messages for the same user
                        // go to the same partition (and hence are delivered in order with respect
                        // to that user), we configure a partition key strategy that extracts the
                        // name as the partition key.
                        .withProperty(KafkaProperties.partitionKeyStrategy(), Message::getId)
        ).withAutoAcl(true);
    }
}
