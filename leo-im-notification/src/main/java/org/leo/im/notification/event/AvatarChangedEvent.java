package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 头像改变事件
 * 
 * @author Leo
 * @date 2018/5/14
 */
public class AvatarChangedEvent implements NotificationEvent {
    
    private String userId;

    private String avatar;

    public AvatarChangedEvent(String userId, String avatar) {
        this.userId = userId;
        this.avatar = avatar;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.AVATAR_CHANGED);
        message.put("userId", userId);
        message.put("avatar", avatar);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
