package org.leo.im.http.file;

/**
 * 头像存储工厂类
 * 
 * @author Leo
 * @date 2018/5/11
 */
public final class AvatarStorageFactory {
    
    /**
     * 创建头像存储类的实例
     * @return
     */
    public static AvatarStorage newInstance() {
        if("local".equalsIgnoreCase(System.getProperty("file.settings.driver"))) {
            return new LocalAvatarStorage();
        }
        return new LocalAvatarStorage();
    }

}
