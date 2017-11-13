package com.example.generator.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.example.generator.impl.entity.Mailbox;
import com.example.generator.impl.entity.MessageSent;
import com.example.generator.api.KafkaMessageGeneratorService;
import com.example.generator.api.Message;
import com.example.generator.impl.entity.SendMessage;
import com.google.common.base.Stopwatch;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class KafkaMessageGeneratorServiceImpl implements KafkaMessageGeneratorService {
    private static final PSequence<String> ENTITY_IDS = TreePVector.<String>empty()
            .plus("Alice")
            .plus("Bob")
            .plus("Carol")
            .plus("David")
            .plus("Erin")
            .plus("Frank");
    private static final int CONCURRENT_ASKS = 10;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final Materializer materializer;

    @Inject
    public KafkaMessageGeneratorServiceImpl(
            PersistentEntityRegistry persistentEntityRegistry,
            Materializer materializer
    ) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.materializer = materializer;
        persistentEntityRegistry.register(Mailbox.class);
    }

    @Override
    public ServiceCall<NotUsed, Done> generate(int count) {
        return request -> {
            log.info("BEGIN generation of " + count + " messages");
            Stopwatch stopwatch = Stopwatch.createStarted();
            return Source.range(1, count)
                    .mapAsync(CONCURRENT_ASKS, i -> {
                        String id = ENTITY_IDS.get((i - 1) % ENTITY_IDS.size());
                        Message m = new Message(id, "Message " + i);
                        PersistentEntityRef<SendMessage> ref =
                                persistentEntityRegistry.refFor(Mailbox.class, id);
                        return ref.ask(new SendMessage(m));
                    })
                    .runWith(Sink.ignore(), materializer)
                    .thenApply(done -> {
                        log.info("END   generation of " + count + " messages (" + stopwatch + ")");
                        return done;
                    });
        };
    }

    @Override
    public Topic<Message> messages() {
        return TopicProducer.taggedStreamWithOffset(MessageSent.TAG.allTags(), (tag, offset) ->
                persistentEntityRegistry.eventStream(tag, offset)
                        .map(eventAndOffset ->
                                Pair.create(eventAndOffset.first().getMessage(), eventAndOffset.second())
                        )
        );
    }
}
