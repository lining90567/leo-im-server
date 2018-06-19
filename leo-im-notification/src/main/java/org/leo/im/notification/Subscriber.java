package org.leo.im.notification;

/**
 * 订阅者接口
 * 
 * @author Leo
 * @date 2018/3/28
 */
public interface Subscriber {
    
    /**
     * 接收数据
     * @param channel
     * @param message
     */
    void onMessage(String channel, String message);

}
