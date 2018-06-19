package org.leo.im.service.support;

/**
 * 缓存注解持有者
 * 
 * @author Leo
 * @date 2018/3/19
 */
public class CacheableHolder {

    private static final ThreadLocal<Boolean> THREADLOCAL = new ThreadLocal<Boolean>();
    
    /**
     * 设置是否启用缓存注解
     * @param cacheable
     */
    public static void setCacheable(boolean cacheable) {
        THREADLOCAL.set(cacheable);
    }
    
    /**
     * 得到是否启用缓存注解
     * @return
     */
    public static boolean getCacheable() {
        Boolean cacheable = THREADLOCAL.get();
        return cacheable == null ? false : cacheable;
    }
    
    /**
     * 删除是否启用缓存注解
     */
    public static void remove() {
        THREADLOCAL.remove();
    }
    
}
