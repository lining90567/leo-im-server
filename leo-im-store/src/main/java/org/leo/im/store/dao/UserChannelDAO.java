package org.leo.im.store.dao;

import java.util.List;

import org.leo.im.model.UserChannel;

/**
 * 用户频道dao接口
 * 
 * @author Leo
 * @date 2018/4/20
 */
public interface UserChannelDAO {

    /**
     * 根据用户id得到用户频道列表
     * @param userId
     * @param type
     * @param limit
     * @return
     */
    List<UserChannel> listByUserId(String userId, String type, int limit);
    
    /**
     * 添加用户频道
     * @param userChannel
     * @return
     */
    int save(UserChannel userChannel);
    
    /**
     * 批量添加用户频道
     * @param userChannels
     * @return
     */
    int batchSave(List<UserChannel> userChannels);
    
    /**
     * 得到用户频道
     * @param userId
     * @param channelId
     * @return
     */
    UserChannel get(String userId, String channelId);
    
    /**
     * 更新用户频道显示名
     * @param channelId
     * @param userId
     * @param displayName
     * @return
     */
    int updateDisplayName(String channelId, String userId, String displayName);
    
    /**
     * 删除用户频道
     * @param userId
     * @param channelId
     * @return
     */
    int remove(String userId, String channelId);
    
    /**
     * 根据名称得到用户频道列表
     * @param userId
     * @param name
     * @param type
     * @return
     */
    List<UserChannel> listByName(String userId, String name, String type);
    
}
