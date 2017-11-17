package com.example.consumer.beta.impl;

import com.example.generator.api.KafkaMessageGeneratorService;
import com.example.consumer.api.KafkaMessageConsumerService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class KafkaMessageConsumerModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        // Bind the KafkaMessageConsumerService service
        bindService(KafkaMessageConsumerService.class, KafkaMessageConsumerServiceImpl.class);
        // Bind the KafkaMessageGeneratorService client
        bindClient(KafkaMessageGeneratorService.class);
        // Bind the subscriber eagerly to ensure it starts up
        bind(KafkaMessageConsumer.class).asEagerSingleton();
    }
}
