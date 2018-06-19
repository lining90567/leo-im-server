package org.leo.im.http.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于Java Map的缓存管理器
 * 
 * @author Leo
 * @date 2018/3/27
 */
final class MapCacheManager implements CacheManager {

    private final static int CLEAR_INTERVAL = 10;

    private Map<String, Cache> caches = new ConcurrentHashMap<>(64);

    private MapCacheManager() {

    }
    
    public static class InstanceHolder {
        private static MapCacheManager instance = new MapCacheManager();
        static {
            instance.clearExpired();
        }
    }
    
    public static MapCacheManager getInstance() {
        return InstanceHolder.instance;
    }
    
    /**
     * 得到缓存
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        Cache cache = caches.get(key);
        return cache == null ? null : cache.getValue();
    }

    /**
     * 添加缓存
     * 
     * @param key
     * @param value
     */
    @Override
    public void put(String key, Object value) {
        Cache cache = new Cache(value);
        caches.put(key, cache);
    }

    /**
     * 添加缓存
     * 
     * @param key
     * @param value
     * @param timeout
     *            生存时间，单位为秒。
     */
    @Override
    public void put(String key, Object value, int timeout) {
        Cache cache = new Cache(value, timeout);
        caches.put(key, cache);
    }

    /**
     * 删除缓存
     * 
     * @param key
     */
    @Override
    public void remove(String key) {
        this.caches.remove(key);
    }

    /**
     * 清除缓存
     */
    @Override
    public void clear() {
        this.caches.clear();
    }

    /**
     * 清理超时缓存
     */
    @Override
    public void clearExpired() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Set<Entry<String, Cache>> entrySet = caches.entrySet();
                long currentTime = System.currentTimeMillis();
                for (Entry<String, Cache> entry : entrySet) {
                    if (entry.getValue().getExpireAt() > 0 && currentTime >= entry.getValue().getExpireAt()) {
                        caches.remove(entry.getKey());
                    }
                }
            }

        }, 1 * 1000, CLEAR_INTERVAL * 1000);
    }

}
