package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 创建频道事件处理器
 * 
 * @author Leo
 * @date 2018/5/27
 */
public class ChannelCreatedEvent implements NotificationEvent {
    
    private String channelId;
    
    private String userIds;

    public ChannelCreatedEvent(String channelId, String userIds) {
        this.channelId = channelId;
        this.userIds = userIds;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.CREATE_CHANNEL);
        message.put("channelId", this.channelId);
        message.put("userIds", this.userIds);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
