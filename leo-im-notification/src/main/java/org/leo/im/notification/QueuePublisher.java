package org.leo.im.notification;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列发布者实现类
 * 
 * @author Leo
 * @date 2018/3/30
 */
public class QueuePublisher implements Publisher {

    private static final int QUEUE_CAPACITY;

    private static final Logger logger = LoggerFactory.getLogger(QueuePublisher.class);

    static {
        Integer queueCapacity = Integer.getInteger("notification.capacity");
        QUEUE_CAPACITY = (queueCapacity == null ? 10240 : queueCapacity);
    }

    /**
     * 包含频道的哈希表
     */
    private Map<String, BlockingQueue<String>> channels = new ConcurrentHashMap<>(2);

    private QueuePublisher() {

    }

    private static class InstanceHolder {
        private static QueuePublisher instance = new QueuePublisher();
    }

    public static QueuePublisher getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 发布消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void publish(String channel, String message) {
        BlockingQueue<String> queue = channels.get(message);
        if (queue == null) {
            queue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
            BlockingQueue<String> returnQueue = this.channels.putIfAbsent(channel, queue);
            if (returnQueue != null) {
                queue = returnQueue;
            }
        }
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 订阅频道
     * 
     * @param subscriber
     * @param subscribeChannels
     */
    @Override
    public void subscribe(Subscriber subscriber, String... subscribeChannels) {
        for (String channel : subscribeChannels) {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
            BlockingQueue<String> returnQueue = this.channels.putIfAbsent(channel, queue);
            final BlockingQueue<String> subscribeQueue = returnQueue != null ? returnQueue : queue;
            Thread thread = new Thread(() -> {
                for (;;) {
                    final String message = takeFromQueue(subscribeQueue);
                    if (message != null) {
                        ThreadPoolHolder.getThreadPool().execute(() -> {
                            subscriber.onMessage(channel, message);
                        });
                    }
                }
            });
            thread.setName("SubscriberThread-" + channel);
            thread.start();
        }
    }

    /**
     * 从队列中获取数据
     * 
     * @param subscribeQueue
     * @return
     */
    private String takeFromQueue(BlockingQueue<String> subscribeQueue) {
        try {
            return subscribeQueue.take();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
