package com.example.consumer.impl;

import akka.stream.javadsl.Flow;
import com.example.consumer.impl.entity.DummyEntity;
import com.example.consumer.impl.entity.MessageCommand;
import com.example.generator.api.KafkaMessageGeneratorService;
import com.example.generator.api.Message;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class KafkaMessageConsumer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    private PersistentEntityRegistry entityRegistry;


    @Inject
    public KafkaMessageConsumer(
            KafkaMessageGeneratorService kafkaMessageGeneratorService,
            PersistentEntityRegistry entityRegistry) {

        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
        this.entityRegistry = entityRegistry;

        entityRegistry.register(DummyEntity.class);
    }


    void start() {
        kafkaMessageGeneratorService.messages()
                .subscribe()
                .atLeastOnce(
                        Flow.<Message>create()
                                .mapAsync(
                                        1,
                                        (Message m) -> {
//                                            log.info("Consumed message: " + m);
                                            return entityRegistry
                                                    .refFor(DummyEntity.class, m.getId())
                                                    .ask(new MessageCommand(m.getMessage()));
                                        })
                );

    }
}

