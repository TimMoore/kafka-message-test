package com.example.consumer.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

public interface KafkaMessageConsumerService extends Service {
    /**
     * Example: curl -X POST http://localhost:9000/api/consume-alpha
     */
    ServiceCall<NotUsed, Done> startConsumer();

    @Override
    default Descriptor descriptor() {
        return named("kafka-message-consumer-alpha")
                .withCalls(
                        restCall(Method.POST, "/api/consume-alpha", this::startConsumer)
                ).withAutoAcl(true);
    }
}
