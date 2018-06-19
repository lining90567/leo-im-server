package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 成员数量变更事件
 * 
 * @author Leo
 * @date 2018/6/11
 */
public class MembersCountChangedEvent implements NotificationEvent {
    
    private String channelId;
    
    private int count;
    
    public MembersCountChangedEvent(String channelId, int count) {
        this.channelId = channelId;
        this.count = count;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.MEMBERS_COUNT_CHANGED);
        message.put("channelId", this.channelId);
        message.put("count", this.count);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
