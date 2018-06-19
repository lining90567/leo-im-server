package org.leo.im.http.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import org.leo.im.http.exception.NoSuchSettingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 本地文件存储实现类
 * 
 * @author Leo
 * @date 2018/5/11
 */
class LocalFileStorage extends AbstractLocalFileStorage implements FileStorage {
    
    private static Logger logger = LoggerFactory.getLogger(LocalFileStorage.class);

    /**
     * 保存文件
     * @param key
     * @param fileName
     * @param data
     */
    @Override
    public boolean save(String key, String fileName, byte[] data) {
        StringBuilder newFile = new StringBuilder(256);
        newFile.append(System.getProperty("file.settings.directory"));
        if(newFile.length() == 0) {
            throw new NoSuchSettingException("file.settings.directory");
        }
        if(key != null && !key.trim().isEmpty()) {
            newFile.append("/").append(key);
        }
        // 判断目录是否存在
        File dir = new File(newFile.toString());
        if(!dir.exists()) {
            dir.mkdirs();
        }
        
        // 创建文件
        newFile.append("/").append(fileName);
        File file = new File(newFile.toString());
        OutputStream os = null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
            os.write(data);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }
    
    /**
     * 保存预览图片
     * @param key
     * @param fileName
     * @param data
     * @param width
     * @param height
     * @return
     */
    @Override
    public short[] saveThumb(String key, String fileName, byte[] data, int width, int height) {
        StringBuilder newFile = new StringBuilder(256);
        newFile.append(System.getProperty("file.settings.directory"));
        if(newFile.length() == 0) {
            throw new NoSuchSettingException("file.settings.directory");
        }
        if(key != null && !key.trim().isEmpty()) {
            newFile.append("/").append(key);
        }
        // 判断目录是否存在
        File dir = new File(newFile.toString());
        if(!dir.exists()) {
            dir.mkdir();
        }
        
        newFile.append("/").append(fileName);
        InputStream is = new ByteArrayInputStream(data);
        try {
            Thumbnails.of(is).size(width, height).toFile(newFile.toString());
            File picture = new File(newFile.toString());
            BufferedImage sourceImage = ImageIO.read(new FileInputStream(picture));
            return new short[] { (short)sourceImage.getWidth(), (short)sourceImage.getHeight() };
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public RandomAccessFile read(String fileName) {
        StringBuilder file = new StringBuilder(256);
        file.append(System.getProperty("file.settings.directory"));
        if(file.length() == 0) {
            throw new NoSuchSettingException("file.settings.directory");
        }
        file.append("/").append(fileName);
        try {
            return new RandomAccessFile(file.toString(), "r");
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
