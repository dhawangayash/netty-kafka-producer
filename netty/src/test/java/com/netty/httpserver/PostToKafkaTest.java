package com.netty.httpserver;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.junit.Assert.fail;


public class PostToKafkaTest {
    @Test
    public void testWrite2Kafka() {
        PostToKafka postToKafka = new PostToKafka();
        Mockito.mock(Properties.class);
        try {
            postToKafka.write2Kafka("{\"Hello\" : \"wow\"}");
        }catch (Exception e) {
            fail("Kafka threw an exception");
        }

    }
}
