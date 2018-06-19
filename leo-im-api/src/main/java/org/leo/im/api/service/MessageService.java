package org.leo.im.api.service;

import java.util.List;

import org.leo.im.api.annotation.Transactional;
import org.leo.im.api.dto.FileDTO;
import org.leo.im.api.dto.MessageDTO;

/**
 * 消息服务接口
 * 
 * @author Leo
 * @date 2018/5/16
 */
public interface MessageService {

    /**
     * 得到消息列表
     * @param channelId
     * @param maxCreateAt
     * @param limit
     * @return
     */
    List<MessageDTO> listMessage(String channelId, long maxCreateAt, int limit);
    
    /**
     * 根据id得到消息
     * @param id
     * @return
     */
    MessageDTO getById(long id);
    
    /**
     * 添加消息
     * @param dto
     * @return
     */
    @Transactional
    MessageDTO saveMessage(MessageDTO dto);
    
    /**
     * 批量添加消息
     * @param dtos
     * @return
     */
    @Transactional
    int saveMessage(List<MessageDTO> dtos);
    
    /**
     * 读取消息
     * @param channelId
     * @param userId
     * @param total
     * @return
     */
    @Transactional
    int readMessage(String channelId, String userId, short total);
    
    /**
     * 删除消息
     * @param messageId
     * @param senderId
     * @param channelId
     * @param toUserId
     * @return
     */
    @Transactional
    int removeMessage(long messageId, String senderId, String channelId, String toUserId);
    
    /**
     * 添加文件
     * @param dto
     * @return
     */
    @Transactional
    String saveFile(FileDTO dto);
    
}
