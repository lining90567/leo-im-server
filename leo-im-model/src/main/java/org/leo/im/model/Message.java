package org.leo.im.model;

/**
 * 消息类
 * 
 * @author Leo
 * @date 2018/4/21
 */
public final class Message {
    
    private long id;
    
    private Channel channel;
    
    private User sender;
    
    private long createAt;
    
    /**
     * 消息类型（如：加某某加入群组、某某退出群组）
     */
    private String type;
    
    private String content;
    
    private File file;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Message [id=" + id + ", channel=" + channel + ", sender=" + sender + ", createAt=" + createAt
                + ", type=" + type + ", content=" + content + ", file=" + file + "]";
    }

}
