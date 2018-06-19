package org.leo.im.socket;

import java.util.HashSet;
import java.util.Set;

import org.leo.im.notification.PublisherFactory;
import org.leo.im.socket.subscription.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * WebSocket Server
 * 
 * @author Leo
 * @date 2018/4/3
 */
public class WebSocketServer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    
    /**
     * 监听端口号
     */
    private int port;
    
    /**
     * Boss线程数
     */
    private int bossThreads = 1;
    
    /**
     * Worker线程数
     */
    private int workerThreads = 2;
    
    public WebSocketServer(int port) {
        this.port = port;
    }
    
    public int getBossThreads() {
        return this.bossThreads;
    }
    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }
    
    public int getWorkerThreads() {
        return this.workerThreads;
    }
    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }
    
    /**
     * 启动服务
     * @throws InterruptedException 
     */
    public void start() throws InterruptedException {
        // 参考：https://www.jianshu.com/p/9a97e667cf84    http://www.importnew.com/21561.html   http://wiki.jikexueyuan.com/project/netty-4-user-guide/implement-websocket-chat-function.html
        /*
         * ChannelOption.SO_BACKLOG对应的是TCP/IP协议listen函数中的backlog参数，
         * 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，
         * 多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小 
         */
        // BossGroup处理nio的Accept事件（TCP连接）
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(this.bossThreads);
        // Worker处理nio的Read和Write事件（通道的I/O事件）
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(this.workerThreads);
        
        try {
            // handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行。
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .handler(new LoggingHandler(LogLevel.DEBUG))
            .childHandler(new WebSocketChannelInitializer());
            
            ChannelFuture f = bootstrap.bind(port).sync();
            logger.info("The netty websocket server is now ready to accept requests on port {}", this.port);
            
            // 初始化群组
            // 考虑到群组可能较多，一次性加载占用资源较大，所以在每个用户登录后，将其加载的群组注册到服务中。
//            initChannelGroup();
            ChannelsHolder.getChannelGroups().put("all", new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
            
            // 订阅消息频道，每个队列对应一个线程，如果规模不大，建议一个队列即可。
            Set<String> channelSet = new HashSet<>(3);
            channelSet.add(System.getProperty("channel.private.message"));
            channelSet.add(System.getProperty("channel.group.message"));
            channelSet.add(System.getProperty("channel.system.message"));
            Object[] objs = channelSet.toArray();
            String[] channels = new String[objs.length];
            for(int i = 0; i < objs.length; i++) {
                channels[i] = objs[i].toString();
            }
            PublisherFactory.createPublisher().subscribe(SubscriberFactory.createSubscriber(), channels);
            
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    /**
     * 初始化Netty ChannelGroup
     */
//    private void initChannelGroup() {
//        // 得到所有群组类型的channel
//        Map<String, Object> parameters = new HashMap<>(1);
//        parameters.put("deleteAt", 0);
//        ChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createChannelService());
//        List<ChannelListDTO> dtoList = serviceProxy.listGroupChannel(parameters, 0);
//        for(ChannelListDTO dto : dtoList) {
//            ChannelsHolder.getChannelGroups().put(dto.getId(), new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
//        }
//        ChannelsHolder.getChannelGroups().put("all", new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
//    }

}
