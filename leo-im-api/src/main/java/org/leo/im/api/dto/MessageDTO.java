package org.leo.im.api.dto;

/**
 * 消息dto类
 * 
 * @author Leo
 * @date 2018/5/15
 */
public class MessageDTO {

    private long id;
    
    private String channelId;
    
    private String channelType;

    private long createAt;

    private String type;

    private String senderId;

    private String senderName;

    private String senderNickname;

    private String senderOnlineStatus;

    private String senderAvatarUrl;
    
    private String senderFirstLetterOfName;
    
    private String content;
    
    private String fileId;
    
    private String fileName;
    
    private String fileExtension;
    
    private int fileSize;
    
    private String fileMimeType;
    
    private int imageWidth;
    
    private int imageHeight;
    
    private short imageThumbWidth;
    
    private short imageThumbHeight;    
    
    private String filePath;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderOnlineStatus() {
        return senderOnlineStatus;
    }

    public void setSenderOnlineStatus(String senderOnlineStatus) {
        this.senderOnlineStatus = senderOnlineStatus;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
    }

    public String getSenderFirstLetterOfName() {
        return senderFirstLetterOfName;
    }

    public void setSenderFirstLetterOfName(String senderFirstLetterOfName) {
        this.senderFirstLetterOfName = senderFirstLetterOfName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileMimeType() {
        return fileMimeType;
    }

    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public short getImageThumbWidth() {
        return imageThumbWidth;
    }

    public void setImageThumbWidth(short imageThumbWidth) {
        this.imageThumbWidth = imageThumbWidth;
    }

    public short getImageThumbHeight() {
        return imageThumbHeight;
    }

    public void setImageThumbHeight(short imageThumbHeight) {
        this.imageThumbHeight = imageThumbHeight;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSenderRealAvatarUrl() {
        if("http://".equalsIgnoreCase(this.senderAvatarUrl) || "https://".equalsIgnoreCase(this.senderAvatarUrl)) {
            return this.senderAvatarUrl;
        }
        if(this.senderAvatarUrl != null && !this.senderAvatarUrl.trim().isEmpty()) {
            return this.senderAvatarUrl;
        }
        return null;
    }

    @Override
    public String toString() {
        return "MessageDTO [id=" + id + ", channelId=" + channelId + ", channelType=" + channelType + ", createAt="
                + createAt + ", type=" + type + ", senderId=" + senderId + ", senderName=" + senderName
                + ", senderNickname=" + senderNickname + ", senderOnlineStatus=" + senderOnlineStatus
                + ", senderAvatarUrl=" + senderAvatarUrl + ", senderFirstLetterOfName=" + senderFirstLetterOfName
                + ", content=" + content + ", fileId=" + fileId + ", fileName=" + fileName + ", fileExtension="
                + fileExtension + ", fileSize=" + fileSize + ", fileMimeType=" + fileMimeType + ", imageWidth="
                + imageWidth + ", imageHeight=" + imageHeight + ", imageThumbWidth=" + imageThumbWidth
                + ", imageThumbHeight=" + imageThumbHeight + ", filePath=" + filePath + "]";
    }
    
}
