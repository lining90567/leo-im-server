package org.leo.im.socket.subscription;

import org.leo.im.notification.Subscriber;
import org.leo.im.socket.exception.MessageHandleException;
import org.leo.im.socket.subscription.handler.MessageHandler;
import org.leo.im.socket.subscription.handler.MessageHandlerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 队列订阅器实现类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class QueueSubscriber implements Subscriber {

    /**
     * 接收数据
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(String channel, String message) {
        if(message == null || message.trim().isEmpty()) {
            throw new MessageHandleException("Message is empty");
        }
        JSONObject json = null;
        try {
            json = JSONObject.parseObject(message);
        } catch(Exception e) {
            throw new MessageHandleException("Message is not a valid json format, " + message);
        }
        MessageHandler handler = MessageHandlerFactory.createMessageHandler(json.getString("action"));
        if(handler == null) {
            throw new MessageHandleException("Invalid action, " + json.getString("action"));
        }
        handler.doHandle(channel, json.toJSONString());
    }

}
