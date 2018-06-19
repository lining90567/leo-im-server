package org.leo.im.api.service;

import org.leo.im.api.annotation.Transactional;

/**
 * 未读消息数量服务接口
 * @author Administrator
 *
 */
public interface UnreadMessageCountService {
    
    /**
     * 批量添加未读消息数量
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    @Transactional
    int batchSaveUnreadMessageCount(String[] userIds, String channelId, short total);
    
    /**
     * 更新未读消息数量
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    @Transactional
    int updateUnreadMessageCount(String userId, String channelId, short total);
    
    /**
     * 批量更新未读消息数量
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    int batchUpdateUnreadMessageCount(String[] userIds, String channelId, short total);
    
    /**
     * 批量增加未读消息数量
     * @param userIds
     * @param channelId
     * @param quantity
     * @return
     */
    @Transactional
    int batchIncreaseUnreadMessageCount(String[] userIds, String channelId, short quantity);
    
    /**
     * 增加未读消息数量
     * @param userId
     * @param channelId
     * @param quantity
     * @return
     */
    @Transactional
    int increaseUnreadMessageCount(String userId, String channelId, short quantity);

}
