package com.example.consumer.impl.entity;

import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

/**
 *
 */
@Value
public class MessageState implements Jsonable{
    String msg;
}
