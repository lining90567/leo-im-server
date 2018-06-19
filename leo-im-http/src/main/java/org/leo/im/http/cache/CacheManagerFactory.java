package org.leo.im.http.cache;

/**
 * 缓存管理器工厂类
 * 
 * @author Leo
 * @date 2018/3/27
 */
public final class CacheManagerFactory {

    /**
     * 得到缓存管理器的实例
     * 
     * @return
     */
    public static CacheManager getCacheManager() {
        String cacheType = System.getProperty("cache.type") == null ? "map"
                : System.getProperty("cache.type").toLowerCase();
        switch (cacheType) {
        case "map":
            return MapCacheManager.getInstance();
        default:
            return MapCacheManager.getInstance();
        }
    }

}
