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
import java.util.Arrays;
import java.util.stream.Collectors;

public class KafkaMessageGeneratorServiceImpl implements KafkaMessageGeneratorService {
    private static final PSequence<String> ENTITY_IDS;

    static {
        // build a big, diverse set of entityIds so that all 40 partitions get data
        TreePVector<String> names =
                TreePVector.from(Arrays.asList(
                        "Alice", "Bob", "Carol", "David", "Erin", "Frank",
                        "Gareth", "Helen", "Ivan", "Joseph", "Kyle", "Laura",
                        "Martin", "Neville", "Oscar", "Paula", "Roberta","Sam"
                ));
        TreePVector<String> surnames =
                TreePVector.from(Arrays.asList(
                        "Smith", "Jones", "Nichols", "Seagull", "McNochols",
                        "Badulescu", "Baskey", "Burke", "Fonseca", "Cady",
                        "Willemsen" , "Richard", "Sauber", "Sanders", "Lazarus",
                        "Simpson"
                ));

        ENTITY_IDS = TreePVector.from(names
                .stream()
                .flatMap(name ->
                        surnames
                                .stream()
                                .map(surname -> name + " " + surname)
                ).collect(Collectors.toList()));
    }

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
    public Topic<Message> messagesBeta() {
        return messages();
    }

    @Override
    public Topic<Message> messagesAlpha() {
        return messages();
    }

    private Topic<Message> messages() {
        return TopicProducer.taggedStreamWithOffset(MessageSent.TAG.allTags(), (tag, offset) ->
                persistentEntityRegistry.eventStream(tag, offset)
                        .map(eventAndOffset ->
                                Pair.create(eventAndOffset.first().getMessage(), eventAndOffset.second())
                        )
        );
    }
}
