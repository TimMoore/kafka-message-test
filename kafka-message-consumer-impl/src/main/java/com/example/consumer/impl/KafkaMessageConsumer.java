package com.example.consumer.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import com.example.generator.api.KafkaMessageGeneratorService;
import com.example.generator.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class KafkaMessageConsumer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;

    @Inject
    public KafkaMessageConsumer(KafkaMessageGeneratorService kafkaMessageGeneratorService) {
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
    }

    void start() {
        int groupMaxSize = 50;

        if (started.compareAndSet(false, true)) {
            kafkaMessageGeneratorService.messages()
                    .subscribe()
                    .atLeastOnce(
                            Flow.<Message>create()
                                    .groupedWithin(groupMaxSize, new FiniteDuration(1, TimeUnit.SECONDS))
//                                    .groupedWithin(1, new FiniteDuration(1, TimeUnit.SECONDS))
                                    .mapAsync(1, (List<Message> m) -> {
                                                log.info("GroupSize: " + m.size());
                                                return CompletableFuture.completedFuture(m.size());
                                            }
                                    ).flatMapConcat(groupSize -> {
                                        // Emit as many 'Done' as there were on the group. Not always the group
                                        // will be 'groupMaxSize'
                                        if (groupSize > 0)
                                            return Source.range(1, groupSize).map(i -> Done.getInstance());
                                        else
                                            return Source.empty();
                                    }
                            )
                    );

        }
    }
}

