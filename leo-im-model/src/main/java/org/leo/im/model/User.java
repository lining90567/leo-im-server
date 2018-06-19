package org.leo.im.model;

import java.util.Date;

/**
 * 用户模型类
 * 
 * @author Leo
 * @date 2018/3/30
 */
public final class User {
    
    private String id;
    
    private String name;
    
    private String firstLetterOfName;
    
    private String nickname;
    
    private String salt;
    
    private String password;
    
    private Boolean locked;
    
    private Date createdAt;
    
    private String avatarUr;
    
    private long lastPostAt;
    
    private String onlineStatus;
    
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFirstLetterOfName() {
        return this.firstLetterOfName;
    }
    public void setFirstLetterOfName(String firstLetter) {
        this.firstLetterOfName = firstLetter;
    }
    
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getSalt() {
        return this.salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Boolean getLocked() {
        return this.locked;
    }
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
    
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getLastPostAt() {
        return this.lastPostAt;
    }
    public void setLastPostAt(long lastPostAt) {
        this.lastPostAt = lastPostAt;
    }
    
    public String getAvatarUrl() {
        return this.avatarUr;
    }
    public void setAvatarUrl(String avatarUr) {
        this.avatarUr = avatarUr;
    }
    
    public String getOnlineStatus() {
        return this.onlineStatus;
    }
    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", firstLetterOfName=" + firstLetterOfName + ", nickname="
                + nickname + ", salt=" + salt + ", password=" + password + ", locked=" + locked + ", createdAt="
                + createdAt + ", avatarUr=" + avatarUr + ", onlineStatus=" + onlineStatus + "]";
    }

}
