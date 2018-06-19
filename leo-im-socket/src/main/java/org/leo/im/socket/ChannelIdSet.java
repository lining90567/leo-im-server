package org.leo.im.socket;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Channel Id 集合类 
 * 使用读写锁保证一个用户多个连接保存和删除的同步性
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class ChannelIdSet {

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private Set<String> channelIds = new HashSet<>();

    /**
     * 得到列表长度
     * 
     * @return
     */
    public int size() {
        try {
            this.rwLock.writeLock().lock();
            return this.channelIds.size();
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    /**
     * 得到包含Channel Id的集合
     * 
     * @return
     */
    public Set<String> getSet() {
        try {
            rwLock.readLock().lock();
            return this.channelIds;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 添加Channel Id
     * 
     * @param channelId
     * @return
     */
    public boolean add(String channelId) {
        try {
            rwLock.writeLock().lock();
            return this.channelIds.add(channelId);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * 删除Channel Id
     * 
     * @param channelId
     * @return
     */
    public boolean remove(String channelId) {
        try {
            rwLock.writeLock().lock();
            return this.channelIds.remove(channelId);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

}
