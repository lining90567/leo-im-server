package org.leo.im.socket.subscription.handler;

/**
 * 消息处理器接口
 * 
 * @author Leo
 * @date 2018/3/29
 */
public interface MessageHandler {
    
    /**
     * 处理消息
     * @param channel
     * @param message
     */
    void doHandle(String channel, String message);

}
