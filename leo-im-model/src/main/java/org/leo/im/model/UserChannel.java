package org.leo.im.model;

/**
 * 用户频道类
 * 
 * @author Leo
 * @date 2018/4/20
 */
public final class UserChannel {
    
    private User user;
    
    private Channel channel;
    
    private String displayName;
    
    private User toUser;
    
    private short unreadMessageCount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public short getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(short unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    @Override
    public String toString() {
        return "UserChannel [user=" + user + ", channel=" + channel + ", displayName=" + displayName + ", toUser="
                + toUser + ", unreadMessageCount=" + unreadMessageCount + "]";
    }

}
