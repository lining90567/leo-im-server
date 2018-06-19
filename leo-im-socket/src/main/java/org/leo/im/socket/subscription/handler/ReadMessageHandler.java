package org.leo.im.socket.subscription.handler;

import com.alibaba.fastjson.JSONObject;

/**
 * 读取消息处理器
 * 
 * @author Leo
 * @date 2018/5/22
 */
public class ReadMessageHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSONObject.parseObject(message);
        super.setUsers(json.getString("userId"));
        super.handleMessage(message);
    }
    
}
