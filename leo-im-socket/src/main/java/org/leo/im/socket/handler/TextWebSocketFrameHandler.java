package org.leo.im.socket.handler;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.leo.im.api.dto.UserDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.UserService;
import org.leo.im.notification.event.NotificationEvent;
import org.leo.im.notification.event.OnlineStatusChangedEvent;
import org.leo.im.service.support.ServiceProxy;
import org.leo.im.socket.ChannelsHolder;
import org.leo.im.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import io.jsonwebtoken.Claims;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

/**
 * Http 处理器
 * 
 * @author Leo
 * @date 2018/3/29
 */
public final class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * ChannelInboundHandlerAdapter的channelRead方法处理完消息后不会自动释放消息，
     * 若想自动释放收到的消息，可以使用SimpleChannelInboundHandler。
     */

    private WebSocketServerHandshaker handshaker;

    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            // 处理HTTP请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
            return;
        } 
        if (msg instanceof WebSocketFrame) {
            // 处理WebSocket请求
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }

        // 手动释放消息（SimpleChannelInboundHandler会自动释放）
        // ReferenceCountUtil.release(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        ctx.close();
        String userId = ChannelsHolder.getUserIdByChannelId(channelId);
        ChannelsHolder.removeChannel(channelId);
        
        // 更新用户在线状态
        if(userId != null) {
            UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
            UserDTO dto = new UserDTO();
            dto.setId(userId);
            dto.setOnlineStatus("offline");
            if(serviceProxy.updateUser(dto, false) != null) {
                NotificationEvent event = new OnlineStatusChangedEvent(dto.getId(), dto.getOnlineStatus());
                event.trigger();
            }
        }
        
    }

    /**
     * 处理HTTP请求
     * 
     * @param ctx
     * @param request
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        // WebSocket访问，处理握手升级。
        if (request.headers().get("Connection").equals("Upgrade")) {
            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("", null, true);
            handshaker = wsFactory.newHandshaker(request);
            if (handshaker == null) {
                // 无法处理的WebSocket版本
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return;
            }

            // 验证token
            String token = getRequestParameter(request, "token");
            if (token == null) {
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED,
                        Unpooled.copiedBuffer("Token not found in request url", CharsetUtil.UTF_8));
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                logger.error("Token not found in request url");
                return;
            }
            JSONObject json = null;
            try {
                Claims claims = JwtUtils.parseJWT(token, System.getProperty("jwt.secret"));
                String subject = claims.getSubject();
                json = JSONObject.parseObject(subject);
            } catch (Exception e) {
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.UNAUTHORIZED,
                        Unpooled.copiedBuffer("Token is not available", CharsetUtil.UTF_8));
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                logger.error("Token is not available");
                return;
            }

            // 向客户端发送WebSocket握手，完成握手。
            final String userId = json.getString("userId");
            ChannelFuture channelFuture = handshaker.handshake(ctx.channel(), request);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        future.channel().close();
                        return;
                    }
                    // 加入到ChannelHolders中
                    ChannelsHolder.addChannel(userId, future.channel());
                }
            });
            return;
        }
        // 普通的HTTP访问
        logger.warn("无效的访问: {}", request.uri());
    }

    /**
     * 处理WebSocket请求
     * 
     * @param ctx
     * @param frame
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            ctx.close();
            return;
        }
        // 没有使用WebSocketServerProtocolHandler，所以不会接收到PingWebSocketFrame。
        // if (frame instanceof PingWebSocketFrame) {
        // ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
        // return;
        // }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }

        String request = ((TextWebSocketFrame) frame).text();
        logger.debug("收到客户端发送的数据：" + request);
        // 回复心跳
        if (request.length() == 0) {
            ctx.writeAndFlush(new TextWebSocketFrame(""));
            return;
        }
        this.handleMessage(ctx.channel(), request);
    }

    /**
     * 得到Http请求的Query String
     * 
     * @param req
     * @param name
     * @return
     */
    private static String getRequestParameter(FullHttpRequest req, String name) {
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        Map<String, List<String>> parameters = decoder.parameters();
        Set<Entry<String, List<String>>> entrySet = parameters.entrySet();
        for (Entry<String, List<String>> entry : entrySet) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }
    
    /**
     * 处理消息
     * @param channel
     * @param message
     */
    private void handleMessage(Channel channel, String message) {
        JSONObject json = null;
        try {
            json = JSONObject.parseObject(message);
        } catch(JSONException e) {
            logger.error(e.getMessage());
            return;
        }
        if(!json.containsKey("action")) {
            logger.warn("can not find action.");
            return;
        }
        switch(json.getString("action").toUpperCase()) {
        case "BIND_GROUP_CHANNEL":
            bindChannelToGroupChannel(json, channel);
            break;
        case "REMOVE_CHANNEL_FROM_GROUP":
            removeChannelFromGroupChannel(json, channel);
            break;
        }
    }
    
    /**
     * 绑定channel到ChannelGroup中
     * @param message
     * @param channel
     */
    private void bindChannelToGroupChannel(JSONObject message, Channel channel) {
        String[] groupIds = message.getString("groupIds").split(",");
        for(int i = 0; i < groupIds.length; i++) {
            if(groupIds[i] != null && !groupIds[i].trim().isEmpty()) {
                ChannelsHolder.addChannelToGroup(groupIds[i], channel);
            }
        }
    }
    
    /**
     * 从GroupChannel中删除channel
     * @param message
     * @param channel
     */
    private void removeChannelFromGroupChannel(JSONObject message, Channel channel) {
        String imChannelId = message.getString("channelId");
        ChannelGroup cg = ChannelsHolder.getChannelGroups().get(imChannelId);
        if(cg != null) {
            cg.remove(channel);
        }
    }

}