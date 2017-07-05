
## Simple Netty server connecting to Kafka


`HttpServerNetty4` a simple server based on `NioServerSocketChannel` which creates some worker threads and boss thread. This server initializes the `HttpServerInitializer` 

`HttpServerInitializer` is a `ChannelInitializer` which overrides the `initChannel(SocketChannel ch)` method and orchestrates the following pipeline:

1. `HttpResponseEncoder()`
2. `HttpRequestDecoder()`
3. `HttpObjectAggregator(Short.MAX_VALUE)`
4. `HttpInputHandler` - Kafka producer has been hooked here.
5. `StringDecoder`
6. `StringEncoder`


`HttpInputHandler` a simple `ChannelInBoundHandlerAdapter` that reads the msg and posts the message to Kafka using the Kakfa producer.


### Deployment


```
mvn clean install
```
``` 
java -Dio.netty.recycler.maxCapacity=0 -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG -Dserver=localhost:9092 -Dtopic=test_topic -jar target/netty4-httpserver-0.0.1-jar-with-dependencies.jar com.netty.httpserver.HttpServerNetty4
```

### Issue
Memory LEAK in load testing. I see memory leaks when Kafka broker applies back pressure on the producer. Owing to this there seems to be memory leaks when pumping messages. 

Load tested on 8-10GBps. 
