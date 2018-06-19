package org.leo.im.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 频道dto类
 * 
 * @author Leo
 * @date 2018/4/11
 */
public final class ChannelDTO {

    private String id;

    private String name;

    private String type;

    private int memberCount;
    
    private long createAt;
    
    private String creatorId;
    
    private String fromUserId;
    
    private String fromUsername;
    
    private String fromUserNickname;
    
    private String toUserId;
    
    private String toUsername;
    
    private String toUserNickname;
    
    private String toUserOnlineStatus;
    
    private String purpose;
    
    private List<ChannelMemberDTO> members = new ArrayList<>(128);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getFromUserNickname() {
        return fromUserNickname;
    }

    public void setFromUserNickname(String fromUserNickname) {
        this.fromUserNickname = fromUserNickname;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public String getToUserNickname() {
        return toUserNickname;
    }

    public void setToUserNickname(String toUserNickname) {
        this.toUserNickname = toUserNickname;
    }

    public String getToUserOnlineStatus() {
        return toUserOnlineStatus;
    }

    public void setToUserOnlineStatus(String toUserOnlineStatus) {
        this.toUserOnlineStatus = toUserOnlineStatus;
    }

    public List<ChannelMemberDTO> getMembers() {
        return members;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "ChannelDTO [id=" + id + ", name=" + name + ", type=" + type + ", memberCount=" + memberCount
                + ", createAt=" + createAt + ", creatorId=" + creatorId + ", fromUserId=" + fromUserId
                + ", fromUsername=" + fromUsername + ", fromUserNickname=" + fromUserNickname + ", toUserId=" + toUserId
                + ", toUsername=" + toUsername + ", toUserNickname=" + toUserNickname + ", toUserOnlineStatus="
                + toUserOnlineStatus + ", purpose=" + purpose + ", members=" + members + "]";
    }

}
