package com.example.consumer.impl.entity;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import lombok.Value;

/**
 *
 */
@Value
public class MessageCommand implements PersistentEntity.ReplyType<Done> {
    String msg;
}
