package org.leo.im.store.dao;

/**
 * 隐藏频道dao接口
 * 
 * @author Leo
 * @date 2018/6/13
 */
public interface HideChannelDAO {
    
    /**
     * 添加隐藏频道
     * @param userId
     * @param channelId
     * @return
     */
    int save(String userId, String channelId);
    
    /**
     * 删除隐藏频道
     * @param userId
     * @param channelId
     * @return
     */
    int remove(String userId, String channelId);

}
