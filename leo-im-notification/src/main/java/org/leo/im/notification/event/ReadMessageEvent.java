package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 读取消息事件
 * 
 * @author Leo
 * @date 2018/5/22
 */
public class ReadMessageEvent implements NotificationEvent {
    
    private String userId;
    
    private String channelId;
    
    private short total;
    
    private boolean readAll;
    
    public ReadMessageEvent(String userId, String channelId, short total, boolean readAll) {
        this.userId = userId;
        this.channelId = channelId;
        this.total = total;
        this.readAll = readAll;
    }
    
    /**
     * 触发事件
     */
    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.READ_MESSAGE);
        message.put("userId", userId);
        message.put("channelId", channelId);
        message.put("total", total);
        message.put("readAll", readAll);
        PublisherFactory.createPublisher().publish(PublishKeys.PRIVATE_MESSAGE_CHANNEL, message.toJSONString());
    }

}
