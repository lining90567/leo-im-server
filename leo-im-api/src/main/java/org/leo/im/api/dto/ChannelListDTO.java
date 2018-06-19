package org.leo.im.api.dto;

/**
 * Channel列表DTO
 * 
 * @author Leo
 * @date 2018/3/30
 */
public class ChannelListDTO {

    private String id;

    private String name;
    
    private String displayName;

    private String otherSideOnlineStatus;

    private String type;

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

    public String getOtherSideOnlineStatus() {
        return otherSideOnlineStatus;
    }

    public void setOtherSideOnlineStatus(String otherSideOnlineStatus) {
        this.otherSideOnlineStatus = otherSideOnlineStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChannelListDTO [id=" + id + ", name=" + name + ", displayName=" + displayName
                + ", otherSideOnlineStatus=" + otherSideOnlineStatus + ", type=" + type + "]";
    }
    
}
