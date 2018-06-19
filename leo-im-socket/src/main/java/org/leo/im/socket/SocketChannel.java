package org.leo.im.socket;

import io.netty.channel.Channel;

/**
 * Socket 通道类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class SocketChannel {

    private String userId;

    private Channel channel;
    
    @Override
    public String toString() {
        return "SocketChannel [userId=" + userId + ", channel=" + channel + "]";
    }
    
    public SocketChannel(Channel channel) {
        this.channel = channel;
    }
    
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
}
