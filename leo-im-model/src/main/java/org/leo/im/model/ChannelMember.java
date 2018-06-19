package org.leo.im.model;

/**
 * 频道成员实体类
 * 
 * @author Leo
 * @date 2018/4/11
 */
public final class ChannelMember {
    
    private User user;
    
    private boolean admin;
    
    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    
    public boolean getAdmin() {
        return this.admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    @Override
    public String toString() {
        return "ChannelMember [user=" + user + ", admin=" + admin + "]";
    }


}
