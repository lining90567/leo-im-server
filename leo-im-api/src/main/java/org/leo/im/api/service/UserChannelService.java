package org.leo.im.api.service;

import java.util.List;

import org.leo.im.api.annotation.Transactional;
import org.leo.im.api.dto.UserChannelDTO;

/**
 * 用户频道服务接口
 * 
 * @author Leo
 * @date 2018/4/20
 */
public interface UserChannelService {
    
    /**
     * 得到用户频道列表
     * @param userId
     * @param type
     * @param limit
     * @return
     */
    List<UserChannelDTO> listUserChannel(String userId, String type, int limit);
    
    /**
     * 得到用户频道
     * @param userId
     * @param channelId
     * @return
     */
    UserChannelDTO get(String userId, String channelId);
    
    /**
     * 更新用户频道
     * @param channelId
     * @param userId
     * @param displayName
     * @return
     */
    @Transactional
    int updateDisplayName(String channelId, String userId, String displayName);
    
    /**
     * 隐藏频道
     * @param userId
     * @param channelId
     * @return
     */
    @Transactional
    int hideChannel(String userId, String channelId);
    
    /**
     * 根据名称得到用户频道列表
     * @param userId
     * @param name
     * @param type
     * @return
     */
    List<UserChannelDTO> listByName(String userId, String name, String type);

}
