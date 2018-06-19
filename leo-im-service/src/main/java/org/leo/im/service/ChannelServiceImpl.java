package org.leo.im.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.leo.im.api.dto.ChannelDTO;
import org.leo.im.api.dto.ChannelListDTO;
import org.leo.im.api.dto.ChannelMemberDTO;
import org.leo.im.api.service.ChannelService;
import org.leo.im.common.data.Page;
import org.leo.im.model.Channel;
import org.leo.im.model.ChannelMember;
import org.leo.im.model.User;
import org.leo.im.model.UserChannel;
import org.leo.im.notification.event.ChannelCreatedEvent;
import org.leo.im.notification.event.ChannelNameChangedEvent;
import org.leo.im.notification.event.ChannelRemovedEvent;
import org.leo.im.notification.event.JoinChannelEvent;
import org.leo.im.notification.event.MembersCountChangedEvent;
import org.leo.im.notification.event.NotificationEvent;
import org.leo.im.notification.event.RemoveFromChannelEvent;
import org.leo.im.store.dao.ChannelDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.factory.DAOFactory;
import org.leo.im.util.BeanUtils;

/**
 * 频道服务实现类
 * 
 * @author Leo
 * @date 2018/4/3
 */
public final class ChannelServiceImpl implements ChannelService {

    /**
     * 得到频道列表
     * 
     * @param parameters
     * @param limit
     * @return
     */
    @Override
    public List<ChannelListDTO> listChannel(Map<String, Object> parameters, int limit) {
        List<Channel> list = DAOFactory.createChannelDAO().list(parameters, limit);
        List<ChannelListDTO> dtoList = new ArrayList<>(list.size());
        for (Channel channel : list) {
            ChannelListDTO dto = new ChannelListDTO();
            BeanUtils.copyProperties(channel, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 得到群组频道列表
     * 
     * @param parameters
     * @param limit
     * @return
     */
    @Override
    public List<ChannelListDTO> listGroupChannel(Map<String, Object> parameters, int limit) {
        List<Channel> list = DAOFactory.createChannelDAO().listGroupChannel(parameters, new String[] { "O", "P" },
                limit);
        List<ChannelListDTO> dtoList = new ArrayList<>(list.size());
        for (Channel channel : list) {
            ChannelListDTO dto = new ChannelListDTO();
            BeanUtils.copyProperties(channel, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 添加频道
     * 
     * @param dto
     * @param creatorNickname
     * @return
     */
    @Override
    public ChannelDTO saveChannel(ChannelDTO dto, String creatorNickname) {
        ChannelDAO channelDAO = DAOFactory.createChannelDAO();
        Channel channel = new Channel();
        BeanUtils.copyProperties(dto, channel);
        User creator = new User();
        creator.setId(dto.getCreatorId());
        channel.setCreator(creator);
        if (dto.getFromUserId() != null && !dto.getFromUserId().trim().isEmpty()) {
            User fromUser = new User();
            fromUser.setId(dto.getFromUserId());
            fromUser.setName(dto.getFromUsername());
            fromUser.setNickname(dto.getFromUserNickname());
            channel.setFrom(fromUser);
        }
        if (dto.getToUserId() != null && !dto.getToUserId().trim().isEmpty()) {
            User toUser = new User();
            toUser.setId(dto.getToUserId());
            toUser.setName(dto.getToUsername());
            toUser.setNickname(dto.getToUserNickname());
            channel.setTo(toUser);
        }
        Channel returnChannel = channelDAO.save(channel);
        boolean channelExists = false;
        if (returnChannel == null) {
            // channel已存在，从数据库中查询已存在的channel。
            returnChannel = channelDAO.getByFromAndTo(dto.getFromUserId(), dto.getToUserId());
            channelExists = true;
        }
        if (returnChannel == null) {
            throw new DAOException("Channel not exists");
        }
        if(channelExists && dto.getType().equals("P")) {
            // 删除隐藏频道
            DAOFactory.createHideChannelDAO().remove(dto.getCreatorId(), returnChannel.getId());
        }
        if (!channelExists) {
            // 得到对方用户（ToUser）的在线状态
            if(dto.getType().equals("P")) {
                User toUser = DAOFactory.createUserDAO().getById(dto.getToUserId());
                if(toUser != null) {
                    returnChannel.getTo().setOnlineStatus(toUser.getOnlineStatus());
                }
            }
            
            // 保存频道成员
            List<ChannelMember> members = this.getMembers(dto);
            DAOFactory.createChannelMemberDAO().save(returnChannel.getId(), members);

            // 保存用户频道信息
            List<UserChannel> userChannels = this.getUserChannels(channel, members);
            DAOFactory.createUserChannelDAO().batchSave(userChannels);

            // 保存未读消息数量
            if (dto.getType().equals("P")) {
                // 私聊
                String[] userIds = new String[] { dto.getFromUserId(), dto.getToUserId() };
                DAOFactory.createUnreadMessageCountDAO().batchSave(userIds, returnChannel.getId(), (short)0);
            } else {
                // 群聊
                String[] userIds = new String[dto.getMemberCount()];
                for(int i = 0; i < dto.getMemberCount(); i++) {
                    userIds[i] = dto.getMembers().get(i).getId();
                }
                DAOFactory.createUnreadMessageCountDAO().batchSave(userIds, returnChannel.getId(), (short)0);
            }
            this.publishChannelCreatedEvent(returnChannel, members);
            this.publishJoinChannelEvent(returnChannel, members, creatorNickname, false);
        }
        return getDTOFromChannel(returnChannel);
    }

    /**
     * 根据id得到频道信息
     * 
     * @param id
     * @return
     */
    @Override
    public ChannelDTO getById(String id) {
        Channel channel = DAOFactory.createChannelDAO().getById(id);
        if (channel == null) {
            return null;
        }

        ChannelDTO dto = new ChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        dto.setCreatorId(channel.getCreator().getId());
        return dto;
    }
    
    /**
     * 得到用户是否为频道的管理员
     * @param userId
     * @param channelId
     * @return
     */
    @Override
    public boolean isAdmin(String userId, String channelId) {
        return DAOFactory.createChannelMemberDAO().isAdmin(userId, channelId);
    }
    
    /**
     * 更新频道名称
     * @param channelId
     * @param name
     * @return
     */
    @Override
    public int updateName(String channelId, String name) {
        int count = DAOFactory.createChannelDAO().updateStringField(channelId, "name", name);
        if(count > 0) {
            this.publishNameChangedEvent(channelId, name);
        }
        return count;
    }
    
    /**
     * 更新频道用途
     * @param channelId
     * @param purpose
     * @return
     */
    @Override
    public int updatePurpose(String channelId, String purpose) {
        return DAOFactory.createChannelDAO().updateStringField(channelId, "purpose", purpose);
    }
    
    /**
     * 添加频道成员
     * @param channelId
     * @param userIds
     * @param userNicknames
     * @param admin
     * @return
     */
    @Override
    public int addMember(String channelId, String[] userIds, String[] userNicknames, String admin) {
        List<ChannelMember> members = new ArrayList<>(userIds.length);
        for(int i = 0; i < userIds.length; i++) {
            ChannelMember member = new ChannelMember();
            User user = new User();
            user.setId(userIds[i]);
            user.setNickname(userNicknames[i]);
            member.setUser(user);
            member.setAdmin(false);
            members.add(member);
        }
        int count = DAOFactory.createChannelMemberDAO().save(channelId, members);
        
        // 更新组成员数量
        if(count > 0) {
            ChannelDAO dao = DAOFactory.createChannelDAO();
            dao.increaseMemberCount(channelId, count);
            
            Channel channel = dao.getById(channelId);
            if(channel != null) {
                List<UserChannel> ucs = new ArrayList<>(userIds.length);
                for(String userId : userIds) {
                    UserChannel uc = new UserChannel();
                    uc.setChannel(channel);
                    User user = new User();
                    user.setId(userId);
                    uc.setUser(user);
                    uc.setDisplayName(channel.getName());
                    ucs.add(uc);
                }
                DAOFactory.createUserChannelDAO().batchSave(ucs);
                DAOFactory.createUnreadMessageCountDAO().batchSave(userIds, channelId, (short)0);
                
                this.publishJoinChannelEvent(channel, members, admin, true);
            }
            new MembersCountChangedEvent(channelId, count).trigger();
        }
        return count;
    }
    
    /**
     * 移除组成员
     * @param channelId
     * @param memberId
     * @param memberNickname
     * @param admin
     * @return
     */
    @Override
    public int removeMember(String channelId, String memberId, String memberNickname, String admin) {
        int count = DAOFactory.createChannelMemberDAO().removeMember(channelId, memberId);
        if(count > 0) {
            DAOFactory.createChannelDAO().increaseMemberCount(channelId, -1);
            DAOFactory.createUserChannelDAO().remove(memberId, channelId);
            DAOFactory.createUnreadMessageCountDAO().remove(channelId, memberId);
            new MembersCountChangedEvent(channelId, -1).trigger();
            
            NotificationEvent event = new RemoveFromChannelEvent(channelId, new String[] { memberId }, new String[] { memberNickname },
                    admin, true);
            event.trigger();
        }
        return count;
    }
    
    /**
     * 得到成员列表
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    @Override
    public Page<ChannelMemberDTO> listMember(String channelId, String username, int limit, int offset) {
        Page<ChannelMember> members = DAOFactory.createChannelMemberDAO().listMember(channelId, username, limit, offset);
        List<ChannelMemberDTO> dtos = new ArrayList<>(members.getRows().size());
        for(ChannelMember member : members.getRows()) {
            ChannelMemberDTO dto = new ChannelMemberDTO();
            dto.setId(member.getUser().getId());
            dto.setNickname(member.getUser().getNickname());
            dto.setAdmin(member.getAdmin());
            dtos.add(dto);
        }
        return new Page<ChannelMemberDTO>(members.getTotal(), dtos);
    }
    
    /**
     * 变更频道管理员
     * @param channelId
     * @param memberId
     * @param isAdmin
     * @return
     */
    @Override
    public int changeAdmin(String channelId, String memberId, boolean isAdmin) {
        return DAOFactory.createChannelMemberDAO().changeAdmin(channelId, memberId, isAdmin);
    }
    
    /**
     * 离开频道
     * @param channelId
     * @param memberId
     * @param memberNickname
     * @return
     */
    @Override
    public int leaveChannel(String channelId, String memberId, String memberNickname) {
        int count = DAOFactory.createChannelMemberDAO().removeMember(channelId, memberId);
        if(count > 0) {
            DAOFactory.createChannelDAO().increaseMemberCount(channelId, -1);
            DAOFactory.createUserChannelDAO().remove(memberId, channelId);
            DAOFactory.createUnreadMessageCountDAO().remove(channelId, memberId);
            new MembersCountChangedEvent(channelId, -1).trigger();
        }
        return count;        
    }
    
    /**
     * 删除频道
     * @param channelId
     * @param adminId
     */
    @Override
    public int removeChannel(String channelId, String adminId) {
        // 判断删除群组的管理员是否是群组创建人
        ChannelDAO dao = DAOFactory.createChannelDAO();
        Channel channel = dao.getById(channelId);
        if(channel != null) {
            if(!channel.getCreator().getId().equals(adminId)) {
                return 0;
            }
            int count = dao.remove(channelId);
            if(count > 0) {
                NotificationEvent event = new ChannelRemovedEvent(channelId);
                event.trigger();
            }
            return count;
        }
        return 0;
    }
    
    /**
     * 从Channel得到ChannelDTO
     * @param channel
     * @return
     */
    private ChannelDTO getDTOFromChannel(Channel channel) {
        ChannelDTO dto = new ChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        dto.setCreatorId(channel.getCreator().getId());
        dto.setFromUserId(channel.getFrom() == null ? null : channel.getFrom().getId());
        dto.setFromUsername(channel.getFrom() == null ? null : channel.getFrom().getName());
        dto.setFromUserNickname(channel.getFrom() == null ? null : channel.getFrom().getNickname());
        dto.setToUserId(channel.getTo() == null ? null : channel.getTo().getId());
        dto.setToUsername(channel.getTo() == null ? null : channel.getTo().getName());
        dto.setToUserNickname(channel.getTo() == null ? null : channel.getTo().getNickname());
        dto.setType(channel.getType());
        if(dto.getType().equals("P")) {
            dto.setToUserOnlineStatus(channel.getTo().getOnlineStatus());
        }
        return dto;
    }

    /**
     * 得到频道成员
     * 
     * @param dto
     * @return
     */
    private List<ChannelMember> getMembers(ChannelDTO dto) {
        List<ChannelMember> members = new ArrayList<>(dto.getMemberCount());
        for (ChannelMemberDTO memberDto : dto.getMembers()) {
            ChannelMember member = new ChannelMember();
            User user = new User();
            user.setId(memberDto.getId());
            user.setNickname(memberDto.getNickname());
            member.setUser(user);
            member.setAdmin(memberDto.getAdmin());
            members.add(member);
        }
        return members;
    }

    /**
     * 得到用户频道
     * 
     * @param channel
     * @param members
     * @return
     */
    private List<UserChannel> getUserChannels(Channel channel, List<ChannelMember> members) {
        List<UserChannel> userChannelList = null;
        if (channel.getType().equals("P")) {
            // 私聊
            userChannelList = new ArrayList<>(2);
            UserChannel userChannel1 = new UserChannel();
            userChannel1.setUser(channel.getFrom());
            userChannel1.setChannel(channel);
            userChannel1.setDisplayName(
                    channel.getTo().getNickname() != null && !channel.getTo().getNickname().trim().isEmpty()
                            ? channel.getTo().getNickname() : channel.getTo().getName());
            userChannel1.setToUser(channel.getTo());
            userChannelList.add(userChannel1);

            UserChannel userChannel2 = new UserChannel();
            userChannel2.setUser(channel.getTo());
            userChannel2.setChannel(channel);
            userChannel2.setDisplayName(
                    channel.getFrom().getNickname() != null && !channel.getFrom().getNickname().trim().isEmpty()
                            ? channel.getFrom().getNickname() : channel.getFrom().getName());
            userChannel2.setToUser(channel.getFrom());
            userChannelList.add(userChannel2);
            return userChannelList;
        }

        // 群聊
        userChannelList = new ArrayList<>(members.size());
        for (ChannelMember member : members) {
            UserChannel userChannel = new UserChannel();
            userChannel.setUser(member.getUser());
            userChannel.setChannel(channel);
            userChannel.setDisplayName(channel.getName());
            userChannelList.add(userChannel);
        }
        return userChannelList;
    }
    
    /**
     * 发布频道创建事件
     * @param channel
     * @param members
     */
    private void publishChannelCreatedEvent(Channel channel, List<ChannelMember> members) {
        StringBuilder userIds = null;
        if(channel.getType().equals("P")) {
            // 私聊
            userIds = new StringBuilder(66);
            userIds.append(channel.getFrom().getId()).append(",").append(channel.getTo().getId()).append(",");
        } else {
            userIds = new StringBuilder(33 * channel.getMemberCount());
            for(ChannelMember member : members) {
                userIds.append(member.getUser().getId()).append(",");
            }
        }
        userIds.setLength(userIds.length() - 1);
        NotificationEvent event = new ChannelCreatedEvent(channel.getId(), userIds.toString());
        event.trigger();
    }
    
    /**
     * 发布加入频道事件
     * @param channel
     * @param members
     * @param admin
     * @param sendBroadcast
     */
    private void publishJoinChannelEvent(Channel channel, List<ChannelMember> members, String admin, boolean sendBroadcast) {
        String[] userIds = null;
        String[] userNicknames = null;
        if(channel.getType().equals("G")) {
            userIds = new String[members.size()];
            userNicknames = new String[members.size()];
            for(int i = 0; i < members.size(); i++) {
                userIds[i] = members.get(i).getUser().getId();
                userNicknames[i] = members.get(i).getUser().getNickname();
            }
        } else {
            userIds = new String[] { channel.getTo().getId() };
        }
        NotificationEvent event = new JoinChannelEvent(channel.getId(), userIds, userNicknames, admin, sendBroadcast);
        event.trigger();
    }
    
    /**
     * 发布频道名称改变事件
     * @param channelId
     * @param channelName
     */
    private void publishNameChangedEvent(String channelId, String channelName) {
        NotificationEvent event = new ChannelNameChangedEvent(channelId, channelName);
        event.trigger();
    }

}
