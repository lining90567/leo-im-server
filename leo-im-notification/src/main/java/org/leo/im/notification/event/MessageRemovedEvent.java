package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息被删除事件
 * @author Leo
 * @date 2018/6/5
 */
public class MessageRemovedEvent implements NotificationEvent {
    
    private long messageId;
    
    private String senderId;
    
    private String channelId;
    
    private String toUserId;
    
    public MessageRemovedEvent(long messageId, String senderId, String channelId, String toUserId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.channelId = channelId;
        this.toUserId = toUserId;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.MESSAGE_REMOVED);
        message.put("messageId", this.messageId);
        message.put("senderId", this.senderId);
        message.put("channelId", this.channelId);
        message.put("toUserId", toUserId);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }
    
}
