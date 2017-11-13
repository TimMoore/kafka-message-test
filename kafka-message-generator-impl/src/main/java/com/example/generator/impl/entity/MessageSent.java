package com.example.generator.impl.entity;

import com.example.generator.api.Message;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class MessageSent implements Jsonable, AggregateEvent<MessageSent> {
    public static final AggregateEventShards<MessageSent> TAG = AggregateEventTag.sharded(MessageSent.class, 4);

    Message message;

    @Override
    public AggregateEventTagger<MessageSent> aggregateTag() {
        return TAG;
    }
}
