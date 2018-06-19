package org.leo.im.notification.event;

import org.leo.im.notification.ActionNames;
import org.leo.im.notification.PublishKeys;
import org.leo.im.notification.PublisherFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 加入频道事件
 * 
 * @author Leo
 * @date 2018/5/28
 */
public class JoinChannelEvent implements NotificationEvent {
    
    private String channelId;
    
    private String[] userIds;
    
    private String[] userNicknames;
    
    private String admin;
    
    private boolean sendBroadcast;

    public JoinChannelEvent(String channelId, String[] userIds, String[] userNicknames, String admin, boolean sendBroadcast) {
        this.channelId = channelId;
        this.userIds = userIds;
        this.userNicknames = userNicknames;
        this.admin = admin;
        this.sendBroadcast = sendBroadcast;
    }

    @Override
    public void trigger() {
        JSONObject message = new JSONObject();
        message.put("action", ActionNames.JOIN_CHANNEL);
        message.put("channelId", this.channelId);
        message.put("channelType", "G");
        message.put("admin", this.admin);
        message.put("sendBroadcast", this.sendBroadcast);
        
        JSONArray userIdArray = new JSONArray();
        if(this.userIds != null) {
            for(String userId : this.userIds) {
                JSONObject userIdJSON = new JSONObject();
                userIdJSON.put("id", userId);
                userIdArray.add(userIdJSON);
            }
        }
        message.put("userIds", userIdArray);
        
        JSONArray userNicknameArray = new JSONArray();
        if(this.userNicknames != null) {
            for(String nickname : this.userNicknames) {
                JSONObject nicknameJSON = new JSONObject();
                nicknameJSON.put("nickname", nickname);
                userNicknameArray.add(nicknameJSON);
            }
        }
        message.put("userNicknames", userNicknameArray);
        PublisherFactory.createPublisher().publish(PublishKeys.SYSTEM_MESSAGE_CHANNEL, message.toJSONString());
    }

}
