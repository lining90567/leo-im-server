package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 昵称改变事件
 * 
 * @author Leo
 * @date 2018/5/14
 */
public class NicknameChangedEvent implements NotificationEvent {
    
    private String userId;

    private String nickname;

    public NicknameChangedEvent(String userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.NICKNAME_CHANGED);
        message.put("userId", userId);
        message.put("nickname", nickname);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
