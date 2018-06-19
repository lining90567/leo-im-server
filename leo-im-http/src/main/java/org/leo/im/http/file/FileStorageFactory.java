package org.leo.im.http.file;

/**
 * 文件存储工厂类
 * @author Administrator
 *
 */
public final class FileStorageFactory {

    /**
     * 得到文件存储类的实例
     * @return
     */
    public static FileStorage newInstance() {
        if("local".equalsIgnoreCase(System.getProperty("file.settings.driver"))) {
            return new LocalFileStorage();
        }
        return new LocalFileStorage();
    }
    
}
