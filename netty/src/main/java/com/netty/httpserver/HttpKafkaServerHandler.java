package com.netty.httpserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by dhawangayash on 6/30/17.
 */
public class HttpKafkaServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(HttpKafkaServerHandler.class);

    private KafkaProducer<String, String> kafkaProducer;

    public HttpKafkaServerHandler(KafkaProducer producer) {
        this.kafkaProducer = producer;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    protected void messageRecieved(ChannelHandlerContext channelHandlerContext, Object fullHttpRequest) throws Exception {

        if (fullHttpRequest instanceof FullHttpRequest) {
            FullHttpRequest msgReq = (FullHttpRequest) fullHttpRequest;
            String msg = msgReq.content().toString(UTF_8);
            ProducerRecord<String, String> record = new ProducerRecord("topic", msg);
            LOG.debug("Record:'" + msg + "'");

            Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send(record);
            RecordMetadata recordMetadata = recordMetadataFuture.get();
            LOG.debug("=========== topic ===============" + recordMetadata.topic());
            LOG.debug("=========== offset ===============" + recordMetadata.offset());
            LOG.debug("=========== partition ===============" + recordMetadata.partition());
            LOG.debug("=========== timestamp ===============" + recordMetadata.timestamp());

            kafkaProducer.flush();
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer("SUCCESS".getBytes()));
            channelHandlerContext.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }
}
