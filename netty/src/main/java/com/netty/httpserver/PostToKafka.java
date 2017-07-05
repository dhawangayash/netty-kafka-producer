package com.netty.httpserver;


import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * API that allows to post data to Kafka Topics
 */
public class PostToKafka {
    private static final Logger LOG = LoggerFactory.getLogger(HttpInputHandler.class);

    final Producer<String, String> _producer;
    final static String topic = StringUtils.isNotEmpty(System.getProperty("topic"))?
            System.getProperty("topic"): "localhost_" + System.currentTimeMillis();
    final static String kafkaservers =
            StringUtils.isNotEmpty(System.getProperty("bootstrap.servers"))?
                    System.getProperty("bootstrap.servers"): "localhost:9092";


    public PostToKafka() {
        _producer = KafkaProducerSingleton._INSTANCE.getKafkaProducer();
    }

    public void write2Kafka(String request) throws Exception {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, request);
        Future<RecordMetadata> recordMetadataFuture = _producer.send(record);

        CompletableFuture completableFuture = makeCompletableFuture(recordMetadataFuture);
        completableFuture.thenAccept(x -> {
            RecordMetadata recordMetadata = (RecordMetadata) x;
            LOG.debug("=========== topic ==============="+ recordMetadata.topic());
            LOG.debug("=========== offset ==============="+ recordMetadata.offset());
            LOG.debug("=========== partition ==============="+ recordMetadata.partition());
            LOG.debug("=========== timestamp ==============="+ recordMetadata.timestamp());
        });
        _producer.flush();
        System.out.println("Successfully Pushed to Kafka Topic");
    }

    /**
     * Kafka Java Client
     */
    public enum KafkaProducerSingleton {
        _INSTANCE;
        private Producer<String, String> producer = null;


        public Producer<String, String> getKafkaProducer() {
            if (producer == null) {
                Properties props = new Properties();
                props.put("bootstrap.servers", PostToKafka.kafkaservers);
                props.put("acks", "all");
                props.put("retries", 0);
                props.put("batch.size", 16384);
                props.put("linger.ms", 1);
                props.put("buffer.memory", 33554432);
                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                producer = new KafkaProducer<>(props);
            }
            return producer;
        }
    }

    public static <T> CompletableFuture<RecordMetadata> makeCompletableFuture(Future<RecordMetadata> future) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (InterruptedException| ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
