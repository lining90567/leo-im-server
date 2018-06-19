package org.leo.im.store.dao;

import java.util.List;

import org.leo.im.common.data.Page;
import org.leo.im.model.ChannelMember;

/**
 * 频道成员dao接口
 * 
 * @author Leo
 * @date 2018/4/10
 */
public interface ChannelMemberDAO {

    /**
     * 添加成员
     * @param channelId
     * @param members
     * @return
     */
    int save(String channelId, List<ChannelMember> members);
    
    /**
     * 得到成员列表
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    Page<ChannelMember> listMember(String channelId, String username, int limit, int offset);
    
    /**
     * 得到用户是否为频道管理员
     * @param userId
     * @param channelId
     * @return
     */
    boolean isAdmin(String userId, String channelId);
    
    /**
     * 移除成员
     * @param channelId
     * @param memberId
     * @return
     */
    int removeMember(String channelId, String memberId);
    
    /**
     * 修改管理员
     * @param channelId
     * @param memberId
     * @param isAdmin
     * @return
     */
    int changeAdmin(String channelId, String memberId, boolean isAdmin);
    
}
