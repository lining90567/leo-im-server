package org.leo.im.http.cache;

/**
 * 缓存管理接口
 * 
 * @author Leo
 * @date 2018/3/27
 */
public interface CacheManager {
    
    /**
     * 得到缓存
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 设置缓存
     * @param key
     * @param value
     */
    void put(String key, Object value);
    
    /**
     * 设置缓存
     * @param key
     * @param value
     * @param timeout 生存时间，单位为秒。
     */
    void put(String key, Object value, int timeout);
    
    /**
     * 删除缓存
     * @param key
     */
    void remove(String key);
    
    /**
     * 清除缓存
     */
    void clear();
    
    /**
     * 清理超时缓存
     */
    void clearExpired();
    
}
