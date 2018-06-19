package org.leo.im.service;

import java.util.ArrayList;
import java.util.List;

import org.leo.im.api.dto.UserChannelDTO;
import org.leo.im.api.service.UserChannelService;
import org.leo.im.model.UserChannel;
import org.leo.im.store.factory.DAOFactory;

/**
 * 用户频道服务实现类
 * 
 * @author Lining
 * @date 2018/4/20
 */
public final class UserChannelServiceImpl implements UserChannelService {

    /**
     * 得到用户频道列表
     * @param userId
     * @param type
     * @param limit
     * @return
     */
    @Override
    public List<UserChannelDTO> listUserChannel(String userId, String type, int limit) {
        List<UserChannel> list = DAOFactory.createUserChannelDAO().listByUserId(userId, type, limit);
        List<UserChannelDTO> dtoList = new ArrayList<>(list.size());
        for(UserChannel userChannel : list) {
            UserChannelDTO dto = new UserChannelDTO();
            dto.setChannelId(userChannel.getChannel().getId());
            dto.setChannelName(userChannel.getChannel().getName());
            dto.setChannelType(userChannel.getChannel().getType());
            dto.setChannelDisplayName(userChannel.getDisplayName());
            dto.setCreatorId(userChannel.getChannel().getCreator().getId());
            if(dto.getChannelType().equals("P")) {
                dto.setToUserId(userChannel.getToUser().getId());
                dto.setToUserOnlineStatus(userChannel.getToUser().getOnlineStatus());
            }
            dto.setUnreadMessageCount(userChannel.getUnreadMessageCount());
            dtoList.add(dto);
        }
        return dtoList;
    }
    
    /**
     * 得到用户频道
     * @param userId
     * @param channelId
     * @return
     */
    @Override
    public UserChannelDTO get(String userId, String channelId) {
        UserChannel userChannel = DAOFactory.createUserChannelDAO().get(userId, channelId);
        if(userChannel == null) {
            return null;
        }
        UserChannelDTO dto = new UserChannelDTO();
        dto.setChannelId(userChannel.getChannel().getId());
        dto.setChannelName(userChannel.getChannel().getName());
        dto.setChannelType(userChannel.getChannel().getType());
        dto.setChannelDisplayName(userChannel.getDisplayName());
        dto.setMemberCount(userChannel.getChannel().getMemberCount());
        dto.setCreatorId(userChannel.getChannel().getCreator().getId());
        if(dto.getChannelType().equals("P")) {
            dto.setToUserId(userChannel.getToUser().getId());
            dto.setToUserOnlineStatus(userChannel.getToUser().getOnlineStatus());
        }
        dto.setUnreadMessageCount(userChannel.getUnreadMessageCount());
        return dto;
    }
    
    /**
     * 更新用户频道
     * @param channelId
     * @param userId
     * @param displayName
     * @return
     */
    @Override
    public int updateDisplayName(String channelId, String userId, String displayName) {
        return DAOFactory.createUserChannelDAO().updateDisplayName(channelId, userId, displayName);
    }
    
    /**
     * 隐藏频道
     * @param userId
     * @param channelId
     * @return
     */
    @Override
    public int hideChannel(String userId, String channelId) {
        return DAOFactory.createHideChannelDAO().save(userId, channelId);
    }
    
    /**
     * 根据名称得到用户频道列表
     * @param userId
     * @param name
     * @param type
     * @return
     */
    @Override
    public List<UserChannelDTO> listByName(String userId, String name, String type) {
        List<UserChannel> list = DAOFactory.createUserChannelDAO().listByName(userId, name, type);
        List<UserChannelDTO> dtoList = new ArrayList<>(list.size());
        for(UserChannel userChannel : list) {
            UserChannelDTO dto = new UserChannelDTO();
            dto.setChannelId(userChannel.getChannel().getId());
            dto.setChannelName(userChannel.getChannel().getName());
            dto.setChannelType(userChannel.getChannel().getType());
            dto.setChannelDisplayName(userChannel.getDisplayName());
            dto.setCreatorId(userChannel.getChannel().getCreator().getId());
            if(dto.getChannelType().equals("P")) {
                dto.setToUserId(userChannel.getToUser().getId());
                dto.setToUserOnlineStatus(userChannel.getToUser().getOnlineStatus());
            }
            dto.setUnreadMessageCount(userChannel.getUnreadMessageCount());
            dtoList.add(dto);
        }
        return dtoList;
    }

}