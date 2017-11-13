package com.example.generator.impl.entity;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.example.generator.api.Message;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class MailboxTest {
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("MailboxTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testSendMessage() {
        PersistentEntityTestDriver<SendMessage, MessageSent, NotUsed> driver =
                new PersistentEntityTestDriver<>(system, new Mailbox(), "TestMailbox");

        Message message = new Message("TestMailbox", "Test message");

        Outcome<MessageSent, NotUsed> outcome = driver.run(new SendMessage(message));
        assertEquals(Collections.emptyList(), outcome.issues());
        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals(new MessageSent(message), outcome.events().get(0));
        assertEquals(NotUsed.getInstance(), outcome.state());
    }
}
