package org.leo.im.http.cache;

/**
 * 缓存
 * 
 * @author Leo
 * @date 2018/4/1
 */
final class Cache {
    
    private Object value;
    
    /**
     * 缓存的生存时间，单位：秒
     */
    private int timeout;
    
    private long expireAt;
    
    public Cache(Object value) {
        this.value = value;
    }
    
    public Cache(Object value, int timeout) {
        this.value = value;
        this.timeout = timeout;
        this.expireAt = System.currentTimeMillis() + this.timeout * 1000;
    }
    
    public Object getValue() {
        return this.value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
    public int getTimeout() {
        return this.timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
        this.expireAt = System.currentTimeMillis() + this.timeout * 1000;
    }
    
    public long getExpireAt() {
        return this.expireAt;
    }
    
}
