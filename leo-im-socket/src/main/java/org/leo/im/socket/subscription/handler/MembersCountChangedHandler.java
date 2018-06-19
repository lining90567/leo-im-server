package org.leo.im.socket.subscription.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * 成员数量变更处理器
 * 
 * @author Leo
 * @date 2018/6/11
 */
public class MembersCountChangedHandler extends AbstractMessageHandler implements MessageHandler {
    
    /**
     * 处理消息
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
