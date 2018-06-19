package org.leo.im.store.dao;

import org.leo.im.model.File;

/**
 * 文件dao接口
 * 
 * @author Leo
 * @date 2018/6/13
 */
public interface FileDAO {

    /**
     * 添加文件
     * @param file
     * @return
     */
    String save(File file);
    
}
