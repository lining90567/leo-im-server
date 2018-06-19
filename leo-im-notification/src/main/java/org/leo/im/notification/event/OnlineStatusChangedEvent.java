package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 在线状态改变事件
 * 
 * @author Le
 * @date 2018/5/10
 */
public class OnlineStatusChangedEvent implements NotificationEvent {

    private String userId;

    private String onlineStatus;

    public OnlineStatusChangedEvent(String userId, String onlineStatus) {
        this.userId = userId;
        this.onlineStatus = onlineStatus;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.ONLINE_STATUS_CHANGED);
        message.put("userId", userId);
        message.put("onlineStatus", onlineStatus);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
