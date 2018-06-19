package org.leo.im.socket.subscription;

import org.leo.im.notification.Subscriber;

/**
 * 订阅器工厂类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class SubscriberFactory {

    /**
     * 创建订阅器
     * 
     * @return
     */
    public static Subscriber createSubscriber() {
        switch (System.getProperty("notification.type")) {
        case "queue":
            return new QueueSubscriber();
        default:
            return new QueueSubscriber();
        }
    }

}
