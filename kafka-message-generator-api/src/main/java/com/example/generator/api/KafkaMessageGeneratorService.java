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

    ServiceCall<NotUsed, Done> generate(int count);


    Topic<Message> messagesAlpha();
    Topic<Message> messagesBeta();

    @Override
    default Descriptor descriptor() {
        return named("kafka-message-generator").withCalls(
                restCall(Method.POST, "/api/generate?count", this::generate)
        ).withTopics(
                topic("messages-alpha", this::messagesAlpha)
                        .withProperty(KafkaProperties.partitionKeyStrategy(), Message::getId),
                topic("messages-beta", this::messagesBeta)
                        .withProperty(KafkaProperties.partitionKeyStrategy(), Message::getId)
        ).withAutoAcl(true);
    }
}
