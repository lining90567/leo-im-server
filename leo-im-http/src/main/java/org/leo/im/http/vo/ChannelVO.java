package org.leo.im.http.vo;

import java.util.ArrayList;
import java.util.List;

import org.leo.im.api.dto.ChannelMemberDTO;

/**
 * 频道vo类
 * @author Leo
 * @date 2018/4/12
 */
public final class ChannelVO {
    
    private String id;

    private String name;
    
    private String displayName;

    private String type;

    private int memberCount;
    
    private String otherSideId;
    
    private String otherSideOnlineStatus;
    
    private String creatorId;
    
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getOtherSideId() {
        return otherSideId;
    }

    public void setOtherSideId(String otherSideId) {
        this.otherSideId = otherSideId;
    }

    public String getOtherSideOnlineStatus() {
        return otherSideOnlineStatus;
    }

    public void setOtherSideOnlineStatus(String otherSideOnlineStatus) {
        this.otherSideOnlineStatus = otherSideOnlineStatus;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<ChannelMemberDTO> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "ChannelVO [id=" + id + ", name=" + name + ", displayName=" + displayName + ", type=" + type
                + ", memberCount=" + memberCount + ", otherSideId=" + otherSideId + ", otherSideOnlineStatus="
                + otherSideOnlineStatus + ", creatorId=" + creatorId + ", members=" + members + "]";
    }
    
}
