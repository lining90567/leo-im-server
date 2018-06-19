package org.leo.im.notification;

/**
 * 发布key常量类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class PublishKeys {

    /**
     * 私聊消息频道
     */
    public static final String PRIVATE_MESSAGE_CHANNEL;
    
    /**
     * 群聊消息频道
     */
    public static final String GROUP_MESSAGE_CHANNEL;
    
    /**
     * 系统消息频道
     */
    public static final String SYSTEM_MESSAGE_CHANNEL;
    
    static {
        PRIVATE_MESSAGE_CHANNEL = System.getProperty("channel.private.message") == null ? "im-channel" : 
            System.getProperty("channel.private.message");
        
        GROUP_MESSAGE_CHANNEL = System.getProperty("channel.group.message") == null ? "im-channel" : 
            System.getProperty("channel.group.message");
        
        SYSTEM_MESSAGE_CHANNEL = System.getProperty("channel.system.message") == null ? "im-channel" : 
            System.getProperty("channel.system.message");
    }
    
}
