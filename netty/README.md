
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


java 
-Dio.netty.recycler.maxCapacity=0 
-Dio.netty.leakDetectionLevel=advanced 
-Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG 
-Dbootstrap.servers=localhost:9092 
-Dtopic=test_topic 
-jar target/netty4-httpserver-0.0.1-jar-with-dependencies.jar com.netty.httpserver.HttpServerNetty4
```

### Issue
~~Memory LEAK in load testing. I see memory leaks when Kafka broker applies back pressure on the producer. Owing to this there seems to be memory leaks when pumping messages. ~~

### Fix
This issue was fixed and the PR has the updated fix. Fixed using the following links:
- [release msg](https://groups.google.com/forum/#!topic/netty/1ABu4i72sP8)
- [release bytebuf](https://stackoverflow.com/questions/23539854/leak-bytebuf-release-was-not-called-before-its-garbage-collected)


Load tested on 8-10GBps. 
