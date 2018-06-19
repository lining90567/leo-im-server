package org.leo.im.socket.subscription.handler;

import java.util.Set;

import org.leo.im.socket.ChannelIdSet;
import org.leo.im.socket.ChannelsHolder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;

/**
 * 频道创建消息处理器
 * 
 * @author Leo
 * @date 2018/5/28
 */
public class ChannelCreatedHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSON.parseObject(message);
        if (!json.containsKey("userIds")) {
            return;
        }
        
        String channelId = json.getString("channelId");
        String[] userIds = json.getString("userIds").split(",");
        for (String userId : userIds) {
            ChannelIdSet channelIdSet = ChannelsHolder.getChannelsByUserId(userId);
            if (channelIdSet != null) {
                Set<String> imChannelIds = channelIdSet.getSet();
                for (String imChannelId : imChannelIds) {
                    Channel imChannel = ChannelsHolder.getChannelById(imChannelId);
                    if(imChannel == null) {
                        continue;
                    }
                    ChannelsHolder.addChannelToGroup(channelId, imChannel);       
                }
            }
        }
        super.setUsers(json.getString("userIds").split(","));
    }

}
