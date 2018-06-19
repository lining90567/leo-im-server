package org.leo.im.api.dto;

/**
 * 频道成员dto类
 * 
 * @author Leo
 * @date 2018/4/11
 */
public class ChannelMemberDTO {
    
    private String id;
    
    private String nickname;
    
    private boolean admin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "ChannelMemberDTO [id=" + id + ", nickname=" + nickname + ", admin=" + admin + "]";
    }

}
