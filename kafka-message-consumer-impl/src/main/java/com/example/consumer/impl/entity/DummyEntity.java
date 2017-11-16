package com.example.consumer.impl.entity;


import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

public class DummyEntity extends PersistentEntity<MessageCommand, MessageEvent, MessageState> {

    @Override
    public Behavior initialBehavior(Optional<MessageState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(new MessageState(""));
        b.setCommandHandler(
                MessageCommand.class,
                (MessageCommand cmd, CommandContext<Done> ctx) ->
                        ctx.thenPersist(
                                new MessageEvent("asdf " + cmd.getMsg()),
                                (MessageEvent evt) -> ctx.reply(Done.getInstance())
                        )
        );
        b.setEventHandler(MessageEvent.class, evt -> new MessageState(evt.getMsg()));
        return b.build();
    }
}
