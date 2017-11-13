package com.example.generator.impl.entity;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

public class Mailbox extends PersistentEntity<SendMessage, MessageSent, NotUsed> {
    @Override
    public Behavior initialBehavior(Optional<NotUsed> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(NotUsed.getInstance());

        b.setCommandHandler(SendMessage.class, (cmd, ctx) ->
                ctx.thenPersist(new MessageSent(cmd.getMessage()),
                        evt -> ctx.reply(Done.getInstance())));

        b.setEventHandler(MessageSent.class, evt -> NotUsed.getInstance());

        return b.build();
    }

}
