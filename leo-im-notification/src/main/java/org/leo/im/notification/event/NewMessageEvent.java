package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 收到新消息事件
 * 
 * @author Leo
 * @date 2018/5/16
 */
public class NewMessageEvent implements NotificationEvent {
    
    private JSONObject message;

    public NewMessageEvent(JSONObject message) {
        this.message = message;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        message.put("action", ActionNames.NEW_MESSAGE);
        // 群发、私聊、系统消息
        if(message.containsKey("type")) {
            PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
            return;
        }
        if(message.containsKey("userIds")) {
            PublisherFactory.createPublisher().publish(PublishKeys.PRIVATE_MESSAGE_CHANNEL, message.toJSONString());
            return;
        }
        PublisherFactory.createPublisher().publish(PublishKeys.GROUP_MESSAGE_CHANNEL, message.toJSONString());
    }

}
