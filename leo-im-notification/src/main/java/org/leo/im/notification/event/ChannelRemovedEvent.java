package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 频道被删除事件
 * 
 * @author Leo
 * @date 2018/6/12
 */
public class ChannelRemovedEvent implements NotificationEvent {
    
    private String channelId;
    
    public ChannelRemovedEvent(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.CHANNEL_REMOVED);
        message.put("channelId", this.channelId);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
