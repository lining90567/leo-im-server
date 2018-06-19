package org.leo.im.store.dao;

import java.util.List;

import org.leo.im.model.Message;

/**
 * 消息dao接口
 * 
 * @author Leo
 * @date 2018/4/21
 */
public interface MessageDAO {
    
    /**
     * 得到消息列表
     * @param channelId
     * @param maxCreateAt
     * @param limit
     * @return
     */
    List<Message> listMessage(String channelId, long maxCreateAt, int limit);
    
    /**
     * 根据id得到消息
     * @param id
     * @return
     */
    Message getById(long id);
    
    /**
     * 添加消息
     * @param message
     * @return
     */
    long save(Message message);
    
    /**
     * 批量添加消息
     * @param messages
     * @return
     */
    int save(List<Message> messages);
    
    /**
     * 删除消息
     * @param messageId
     * @param userId
     * @return
     */
    int remove(long messageId, String userId);

}
