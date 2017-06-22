# Interface EventLoop

# Channel
## NioSocketChannel: 
Ths of this as essentiallly abstraction for the Socket API in java


# ChannelFuture
`ChannelFutureListener`'s have specific callbacks in them which are registered to the `ChannelFuture` by the method `addListener()`

So the future is a placeholder for the actual value. All `methods/callbacks` that need to be called are called in the way they were registered.


#ChannelHandler and ChannelPipeline

* `ChannelInitializer` implementation is registered with a `ServerBootstrap`.
* The `ChannelInitializer` installs a custom set of `ChannelHandlers` in the pipeline, when `ChannelInitializer.initChannel()`  during serverBootstraping.
* After the `ChannelInitializer` has installed those custom `ChannelHandlers` it removes itself from the `ChannelPipleline`.

Details on how `ChannelHandlers` and `ChannelPipeline` work together. 

`ChannelPipeline` has a 
head and a tail. The head is the Socket facing side (Endpoint side) and the tail is the kafka facing point (Server side). 

![alt text](file:///Users/dhawangayash/Desktop/netty.png "hello world")

### Flow of a message inside a ChannelPipeline:
1. Message enters through the head of the pipeline. 
2. Message passed to the first `ChannelInboundHandler`. Handler's have the ability to modify the message itself. 
3. The pipeline iterates through all the `ChannelHandler`s that have been registered in that pipeline and these methods are executed in sequence in which they were registered.
4. Once message reaches the tail all processing is terminated. (*Remember all of this is done by the netty*)


##ChannelHandler
`ChannelHandlers`s are of two kinds as given in the image below

![alt text](file:///Users/dhawangayash/Desktop/Netty_ChannelHandlers.png "ChannelHandlers")


`ChannelHandler` are assigned a `ChannelHandlerContext`  are added to a `ChannelPipeline` 

`ChannelHandler`s when added to the `ChannelPipeline` are assigned a `ChannelHandlerContext`.

#### Message passing in ChannelPipeline
1. A `ChannelHandler` could send the message directly via the `Channel` 
2. Or write to the `ChannelHandlerContext` object associated with the `ChannelHandler`.


First method - causes the message to start from the *tail* of the `ChannelPipeline`  

Second Method - causes the message to originate from the **next** handler in the ChannelPipeline.

`ChannelHandlers` have a lot of **_adapters_**. We are mostly interested in `SimpleChannelInboundHandler<T>` [A subclass of `ChannelInboundHandlerAdapter`]

#### SimpleChannelInboundHandler
```
YourBusinessLogic extends SimpleChannelInboundHandler<T>()
```



