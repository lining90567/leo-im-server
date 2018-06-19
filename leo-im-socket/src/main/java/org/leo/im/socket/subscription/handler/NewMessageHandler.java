package org.leo.im.socket.subscription.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * 新消息处理器
 * 
 * @author Leo
 * @date 2018/5/16
 */
public class NewMessageHandler extends AbstractMessageHandler implements MessageHandler {
    
    /**
     * 处理消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSONObject.parseObject(message);
        if(json.containsKey("groupIds")) {
            String[] groupIds = json.getString("groupIds").split(",");
            super.setGroups(groupIds);
        }
        if(json.containsKey("userIds")) {
            String[] userIds = json.getString("userIds").split(",");
            super.setUsers(userIds);
        }
        json.remove("groupIds");
        json.remove("userIds");
        super.handleMessage(json.toJSONString());
    }

}
