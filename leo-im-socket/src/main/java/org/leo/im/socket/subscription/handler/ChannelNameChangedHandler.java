package org.leo.im.socket.subscription.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 频道名称改变处理器
 * 
 * @author Leo
 * @date 2018/6/8
 */
public class ChannelNameChangedHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSON.parseObject(message);
        super.setGroups(json.getString("channelId"));
        super.handleMessage(message);
    }

}
