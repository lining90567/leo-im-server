package org.leo.im.api.service;

import java.util.List;
import java.util.Map;

import org.leo.im.api.annotation.Transactional;
import org.leo.im.api.dto.ChannelDTO;
import org.leo.im.api.dto.ChannelListDTO;
import org.leo.im.api.dto.ChannelMemberDTO;
import org.leo.im.common.data.Page;

/**
 * 频道管理服务接口
 * 
 * @author Leo
 * @date 2018/4/11
 */
public interface ChannelService {
    
    /**
     * 得到频道列表
     * @param parameters
     * @param limit
     * @return
     */
    List<ChannelListDTO> listChannel(Map<String, Object> parameters, int limit);
    
    /**
     * 得到群组频道列表
     * @param parameters
     * @param limit
     * @return
     */
    List<ChannelListDTO> listGroupChannel(Map<String, Object> parameters, int limit);
    
    /**
     * 添加频道
     * @param dto
     * @param creatorNickname
     * @return
     */
    @Transactional
    ChannelDTO saveChannel(ChannelDTO dto, String creatorNickname);
    
    /**
     * 根据id得到频道信息
     * @param id
     * @return
     */
    ChannelDTO getById(String id);
    
    /**
     * 得到用户是否为频道的管理员
     * @param userId
     * @param channelId
     * @return
     */
    boolean isAdmin(String userId, String channelId);
    
    /**
     * 更新频道名称
     * @param channelId
     * @param name
     * @return
     */
    @Transactional
    int updateName(String channelId, String name);
    
    /**
     * 更新频道用途
     * @param channelId
     * @param purpose
     * @return
     */
    @Transactional
    int updatePurpose(String channelId, String purpose);
    
    /**
     * 添加频道成员
     * @param channelId
     * @param userIds
     * @param userNicknames
     * @param admin
     * @return
     */
    @Transactional
    int addMember(String channelId, String[] userIds, String[] userNicknames, String admin);
    
    /**
     * 移除组成员
     * @param channelId
     * @param memberId
     * @param memberNickname
     * @param admin
     * @return
     */
    @Transactional
    int removeMember(String channelId, String memberId, String memberNickname, String admin);
    
    /**
     * 得到成员列表
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    Page<ChannelMemberDTO> listMember(String channelId, String username, int limit, int offset);
    
    /**
     * 变更频道管理员
     * @param channelId
     * @param memberId
     * @param isAdmin
     * @return
     */
    @Transactional
    int changeAdmin(String channelId, String memberId, boolean isAdmin);
    
    /**
     * 离开频道
     * @param channelId
     * @param memberId
     * @param memberNickname
     * @return
     */
    @Transactional
    int leaveChannel(String channelId, String memberId, String memberNickname);
    
    /**
     * 删除频道
     * @param channelId
     * @param adminId
     * @return
     */
    @Transactional
    int removeChannel(String channelId, String adminId);

}
