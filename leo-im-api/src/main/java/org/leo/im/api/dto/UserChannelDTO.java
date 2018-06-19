package org.leo.im.api.dto;

/**
 * 用户频道dto类
 * 
 * @author Leo
 * @date 2018/4/20
 */
public class UserChannelDTO {

    private String channelId;
    
    private String channelName;
    
    private String channelType;
    
    private String channelDisplayName;
    
    private String channelDescription;
    
    private String toUserId;
    
    private String toUserOnlineStatus;
    
    private short unreadMessageCount;
    
    private int memberCount;
    
    private String creatorId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelDisplayName() {
        return channelDisplayName;
    }

    public void setChannelDisplayName(String channelDisplayName) {
        this.channelDisplayName = channelDisplayName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserOnlineStatus() {
        return toUserOnlineStatus;
    }

    public void setToUserOnlineStatus(String toUserOnlineStatus) {
        this.toUserOnlineStatus = toUserOnlineStatus;
    }

    public short getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(short unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return "UserChannelDTO [channelId=" + channelId + ", channelName=" + channelName + ", channelType="
                + channelType + ", channelDisplayName=" + channelDisplayName + ", channelDescription="
                + channelDescription + ", toUserId=" + toUserId + ", toUserOnlineStatus=" + toUserOnlineStatus
                + ", unreadMessageCount=" + unreadMessageCount + ", memberCount=" + memberCount + ", creatorId="
                + creatorId + "]";
    }
    
}
