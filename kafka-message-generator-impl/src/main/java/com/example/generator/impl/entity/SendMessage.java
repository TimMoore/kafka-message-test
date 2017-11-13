package com.example.generator.impl.entity;

import akka.Done;
import com.example.generator.api.Message;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity.ReplyType;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class SendMessage implements Jsonable, ReplyType<Done> {
    Message message;
}
