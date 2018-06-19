package org.leo.im.store.dao;

/**
 * 未读消息数量dao接口
 * 
 * @author Leo
 * @date 2018/4/26
 */
public interface UnreadMessageCountDAO {
    
    /**
     * 添加未读消息
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    int save(String userId, String channelId, short total);
    
    /**
     * 批量添加未读消息
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    int batchSave(String[] userIds, String channelId, short total);
    
    /**
     * 更新未读消息数量
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    int update(String userId, String channelId, short total);
    
    /**
     * 批量更新未读消息数量
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    int batchUpdate(String[] userIds, String channelId, short total);
    
    /**
     * 增加未读消息数
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    int increase(String userId, String channelId, short total);
    
    /**
     * 批量增加未读消息数
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    int batchIncrease(String[] userIds, String channelId, short total);
    
    /**
     * 增加群组频道的未读消息数
     * @param channelId
     * @param exceptiveUserIds
     * @param total
     * @return
     */
    int increaseGroupChannel(String channelId, String[] exceptiveUserIds, short total);
    
    /**
     * 减少未读消息数量
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    int decrease(String userId, String channelId, short total);
    
    /**
     * 删除未读消息数量
     * @param channelId
     * @param userId
     * @return
     */
    int remove(String channelId, String userId);

}
