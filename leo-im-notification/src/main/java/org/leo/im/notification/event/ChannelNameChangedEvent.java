package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 频道名称改变事件
 * 
 * @author Leo
 * @date 2018/6/8
 */
public class ChannelNameChangedEvent implements NotificationEvent {
    
    private String channelId;
    
    private String channelName;
    
    public ChannelNameChangedEvent(String channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.CHANNEL_NAME_CHANGED);
        message.put("channelId", this.channelId);
        message.put("channelName", this.channelName);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
