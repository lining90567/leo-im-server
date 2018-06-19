package org.leo.im.socket;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.leo.im.socket.handler.TextWebSocketFrameHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.RejectedExecutionHandlers;

/**
 * WebSocket 通道初始化类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    
    /**
     * 业务线程池线程数
     */
    private static int eventExecutorGroupThreads = 0;
    
    /**
     * 业务线程池队列长度
     */
    private static int eventExecutorGroupQueues = 0;
    
    static {
        eventExecutorGroupThreads = Integer.getInteger("websocket.executor.threads", 0);
        if(eventExecutorGroupThreads == 0) {
            eventExecutorGroupThreads = Runtime.getRuntime().availableProcessors();
        }
        
        eventExecutorGroupQueues = Integer.getInteger("websocket.executor.queues", 0);
        if(eventExecutorGroupQueues == 0) {
            eventExecutorGroupQueues = 512;
        }
    }
    
    /**
     * 业务线程组
     */
    private static final EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(
            eventExecutorGroupThreads, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "WebSocketRequestHandlerThread_" + this.threadIndex.incrementAndGet());
                }
            }, eventExecutorGroupQueues, RejectedExecutionHandlers.reject()); 

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // WebSocket协议本身是基于HTTP协议的，所以要使用HTTP解编码器
        pipeline.addLast(new HttpServerCodec());
        // 以块的方式来写的处理器
        pipeline.addLast(new ChunkedWriteHandler());
        // Netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
        pipeline.addLast(new HttpObjectAggregator(8192));
        // 文本消息处理器
        pipeline.addLast(eventExecutorGroup, new TextWebSocketFrameHandler());
    }

}
