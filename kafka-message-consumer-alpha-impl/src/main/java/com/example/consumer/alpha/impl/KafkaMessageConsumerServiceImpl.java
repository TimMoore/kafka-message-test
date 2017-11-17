package com.example.consumer.alpha.impl;

import akka.Done;
import akka.NotUsed;
import com.example.consumer.api.KafkaMessageConsumerService;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class KafkaMessageConsumerServiceImpl implements KafkaMessageConsumerService {
    private final KafkaMessageConsumer consumer;

    @Inject
    public KafkaMessageConsumerServiceImpl(KafkaMessageConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ServiceCall<NotUsed, Done> startConsumer() {
        return req -> {
            consumer.start();
            return CompletableFuture.completedFuture(Done.getInstance());
        };
    }
}
