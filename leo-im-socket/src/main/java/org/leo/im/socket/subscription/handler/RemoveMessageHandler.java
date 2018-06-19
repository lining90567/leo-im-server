package org.leo.im.socket.subscription.handler;

import org.leo.im.notification.ActionNames;

import com.alibaba.fastjson.JSONObject;

/**
 * 删除消息处理器
 * 
 * @author Leo
 * @date 2018/6/5
 */
public class RemoveMessageHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSONObject.parseObject(message);
        if(json.containsKey("toUserId") && json.getString("toUserId") != null) {
            super.setUsers(json.getString("toUserId"));
        } else {
            super.setGroups(json.getString("channelId"));
        }
        JSONObject sendMessage = new JSONObject();
        sendMessage.put("action", ActionNames.MESSAGE_REMOVED);
        sendMessage.put("messageId", json.getLongValue("messageId"));
        sendMessage.put("senderId", json.getString("senderId"));        
        super.handleMessage(sendMessage.toJSONString());
    }

}
