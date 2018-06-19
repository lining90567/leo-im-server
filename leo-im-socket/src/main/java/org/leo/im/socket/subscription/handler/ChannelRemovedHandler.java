package org.leo.im.socket.subscription.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * 频道被删除处理器
 * 
 * @author Leo
 * @date 2018/6/12
 */
public class ChannelRemovedHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSONObject.parseObject(message);
        super.setGroups(json.getString("channelId"));
        super.handleMessage(message);
    }

}
