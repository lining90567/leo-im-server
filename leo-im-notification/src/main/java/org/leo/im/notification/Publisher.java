package org.leo.im.notification;

/**
 * 发布者接口
 * 
 * @author Leo
 * @date 2018/3/28
 */
public interface Publisher {
    
    /**
     * 发布消息
     * @param channel
     * @param message
     */
    void publish(String channel, String message);
    
    /**
     * 订阅频道
     * @param subscriber     
     * @param channels
     */
    void subscribe(Subscriber subscriber, String... channels);

}
