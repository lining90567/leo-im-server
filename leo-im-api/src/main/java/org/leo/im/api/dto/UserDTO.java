package org.leo.im.api.dto;

/**
 * 用户dto类
 * 
 * @author Leo
 * @date 2018/3/30
 */
public class UserDTO {

    private String id;

    private String name;
    
    private String firstLetterOfName;

    private String nickname;

    private String password;
    
    private String avatarUrl;
    
    private Boolean locked;
    
    private Long lastPostAt;
    
    private String onlineStatus;

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

    public String getFirstLetterOfName() {
        return firstLetterOfName;
    }

    public void setFirstLetterOfName(String firstLetterOfName) {
        this.firstLetterOfName = firstLetterOfName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Long getLastPostAt() {
        return lastPostAt;
    }

    public void setLastPostAt(Long lastPostAt) {
        this.lastPostAt = lastPostAt;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    @Override
    public String toString() {
        return "UserDTO [id=" + id + ", name=" + name + ", firstLetterOfName=" + firstLetterOfName + ", nickname="
                + nickname + ", password=" + password + ", avatarUrl=" + avatarUrl + ", locked=" + locked
                + ", lastPostAt=" + lastPostAt + ", onlineStatus=" + onlineStatus + "]";
    }

}
