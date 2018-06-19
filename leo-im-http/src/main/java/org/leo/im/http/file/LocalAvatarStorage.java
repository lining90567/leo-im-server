package org.leo.im.http.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.leo.im.http.exception.NoSuchSettingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 本地存储头像实现类
 * 
 * @author Leo
 * @date 2018/5/11
 */
class LocalAvatarStorage extends AbstractLocalFileStorage implements AvatarStorage {
    
    private static Logger logger = LoggerFactory.getLogger(LocalAvatarStorage.class);

    /**
     * 保存头像
     * @param userId
     * @param fileType
     * @param data
     * @param width
     * @param height
     * @return
     */
    @Override
    public boolean save(String userId, String fileType, byte[] data, int width, int height) {
        StringBuilder newFile = new StringBuilder(256);
        newFile.append(System.getProperty("file.settings.directory"));
        if(newFile.length() == 0) {
            throw new NoSuchSettingException("file.settings.directory");
        }
        if(userId != null && !userId.trim().isEmpty()) {
            newFile.append("/").append(userId);
        }
        // 判断目录是否存在
        File dir = new File(newFile.toString());
        if(!dir.exists()) {
            dir.mkdir();
        }
        
        newFile.append("/avatar").append(width).append("x").append(height).append(".").append(fileType);
        InputStream is = new ByteArrayInputStream(data);
        try {
            Thumbnails.of(is).size(width > 70 ? width : width * 2, height > 70 ? height : height * 2).toFile(newFile.toString());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * 读取头像内容
     * @param userId
     * @param fileName
     * @param width
     * @param height
     * @return
     */
    @Override
    public RandomAccessFile read(String userId, String fileName, int width, int height) {
        StringBuilder file = new StringBuilder(256);
        file.append(System.getProperty("file.settings.directory"));
        if(file.length() == 0) {
            throw new NoSuchSettingException("file.settings.directory");
        }
        if(userId != null && !userId.trim().isEmpty()) {
            file.append("/").append(userId);
        }
        String[] fileNameSplit = fileName.split("\\.");
        file.append("/avatar").append(width).append("x").append(height).append(".").append(fileNameSplit[fileNameSplit.length - 1]);
        try {
            return new RandomAccessFile(file.toString(), "r");
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
