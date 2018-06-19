package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 离开频道事件
 * 
 * @author Leo
 * @date 2018/6/11
 */
public class LeaveChannelEvent implements NotificationEvent {
    
    private String channelId;
    
    private String userId;
    
    private String userNickname;
    
    private boolean sendBroadcast;
    
    public LeaveChannelEvent(String channelId, String userId, String userNickname, boolean sendBroadcast) {
        this.channelId = channelId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.sendBroadcast = sendBroadcast;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.LEAVE_CHANNEL);
        message.put("channelId", this.channelId);
        message.put("channelType", "G");
        message.put("sendBroadcast", this.sendBroadcast);
        message.put("userId", this.userId);
        message.put("userNickname", this.userNickname);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());        
    }

}
