package org.leo.im.notification;

import org.leo.im.notification.Publisher;
import org.leo.im.notification.QueuePublisher;

/**
 * 发布者工厂类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class PublisherFactory {

    /**
     * 创建发布者的实例
     * 
     * @return
     */
    public static Publisher createPublisher() {
        switch (System.getProperty("notification.type")) {
        case "queue":
            return QueuePublisher.getInstance();
        default:
            return QueuePublisher.getInstance();
        }
    }

}
