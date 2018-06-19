package org.leo.im.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 频道模型类
 * 
 * @author Leo
 * @date 2018/4/7
 */
public final class Channel {

    private String id;
    
    private String name;
    
    private User from;
    
    private User to;
    
    /**
     * 频道类型
     * G：群聊；P：私聊
     */
    private String type;
    
    private String purpose;
    
    private long createAt;
    
    private long deleteAt;
    
    private long lastPostAt;
    
    private int memberCount;
    
    private User creator;
    
    private List<ChannelMember> members = new ArrayList<>(128);

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

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(long deleteAt) {
        this.deleteAt = deleteAt;
    }

    public long getLastPostAt() {
        return lastPostAt;
    }

    public void setLastPostAt(long lastPostAt) {
        this.lastPostAt = lastPostAt;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<ChannelMember> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "Channel [id=" + id + ", name=" + name + ", from=" + from + ", to=" + to + ", type=" + type
                + ", purpose=" + purpose + ", createAt=" + createAt + ", deleteAt=" + deleteAt + ", lastPostAt="
                + lastPostAt + ", memberCount=" + memberCount + ", creator=" + creator + ", members=" + members + "]";
    }

}
