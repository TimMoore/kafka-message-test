package com.example.generator.impl;

import com.example.generator.api.KafkaMessageGeneratorService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class KafkaMessageGeneratorModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(KafkaMessageGeneratorService.class, KafkaMessageGeneratorServiceImpl.class);
    }
}
