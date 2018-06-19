package org.leo.im.http.file;

import java.io.RandomAccessFile;

/**
 * 文件存储接口
 * 
 * @author Leo
 * @date 2018/5/11
 */
public interface FileStorage {
    
    /**
     * 保存文件
     * @param key
     * @param fileName
     * @param data
     * @return
     */
    boolean save(String key, String fileName, byte[] data);
    
    /**
     * 保存预览图片
     * @param key
     * @param fileName
     * @param data
     * @param width
     * @param height
     * @return
     */
    short[] saveThumb(String key, String fileName, byte[] data, int width, int height);

    /**
     * 读取文件内容
     * @param fileName
     * @return
     */
    RandomAccessFile read(String fileName);
}
