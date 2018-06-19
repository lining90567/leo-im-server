package org.leo.im.http.file;

import java.io.RandomAccessFile;

/**
 * 头像存储接口
 * 
 * @author Leo
 * @date 2018/5/11
 */
public interface AvatarStorage {
    
    /**
     * 保存头像
     * @param userId
     * @param fileType
     * @param data
     * @param width
     * @param height
     * @return
     */
    boolean save(String userId, String fileType, byte[] data, int width, int height);
    
    /**
     * 读取头像内容
     * @param userId
     * @param fileName
     * @param width
     * @param height
     * @return
     */
    RandomAccessFile read(String userId, String fileName, int width, int height);

}
