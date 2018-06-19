package org.leo.im.http.file;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * 本地文件存储抽象类
 * 
 * @author Leo
 * @date 2018/5/12
 */
abstract class AbstractLocalFileStorage {
    
    /**
     * 读取文件内容
     * @param fileName
     * @return
     */
    public RandomAccessFile readFile(String fileName) {
        try {
            return new RandomAccessFile(fileName, "r");
        } catch (FileNotFoundException ignore) {
            return null;
        }
    }

}
