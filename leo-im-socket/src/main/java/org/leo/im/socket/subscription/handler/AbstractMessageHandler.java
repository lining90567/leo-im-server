package org.leo.im.socket.subscription.handler;

import org.leo.im.socket.ChannelIdSet;
import org.leo.im.socket.ChannelsHolder;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 消息处理抽象类
 * 
 * @author Leo
 * @date 2018/5/15
 */
public class AbstractMessageHandler {
    
    private String[] groups = null;
    
    private String[] users = null;
    
    protected void setGroups(String... groups) {
        this.groups = groups;
    }
    
    protected void setUsers(String... users) {
        this.users = users;
    }
    protected String[] getUsers() {
        return this.users;
    }
    
    /**
     * 处理消息
     * @param message
     */
    public void handleMessage(String message) {
        if (this.users != null) {
            for (String user : users) {
                if (user != null && !user.trim().isEmpty()) {
                    ChannelIdSet chIdSet = ChannelsHolder.getChannelsByUserId(user);
                    if (chIdSet == null) {
                        continue;
                    }
                    for (String channelId : chIdSet.getSet()) {
                        Channel ch = ChannelsHolder.getChannelById(channelId);
                        if (ch != null) {
                            ch.writeAndFlush(new TextWebSocketFrame(message));
                        }
                    }
                }
            }
        }
        if (this.groups != null) {
            for (String group : groups) {
                if (group != null && !group.trim().isEmpty()) {
                    ChannelGroup cg = ChannelsHolder.getChannelGroups().get(group);
                    if(cg != null) {
                        cg.writeAndFlush(new TextWebSocketFrame(message));
                    }
                }
            }
        }
    }

}
