package org.leo.im.service;

import java.util.ArrayList;
import java.util.List;

import org.leo.im.api.dto.FileDTO;
import org.leo.im.api.dto.MessageDTO;
import org.leo.im.api.service.MessageService;
import org.leo.im.model.Channel;
import org.leo.im.model.File;
import org.leo.im.model.Message;
import org.leo.im.model.User;
import org.leo.im.notification.event.MessageRemovedEvent;
import org.leo.im.notification.event.NewMessageEvent;
import org.leo.im.notification.event.NotificationEvent;
import org.leo.im.notification.event.ReadMessageEvent;
import org.leo.im.store.dao.UnreadMessageCountDAO;
import org.leo.im.store.factory.DAOFactory;
import org.leo.im.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息服务实现类
 * 
 * @author Leo
 * @date 2018/5/16
 */
public final class MessageServiceImpl implements MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    
    /**
     * 得到消息列表
     * @param channelId
     * @param maxCreateAt
     * @param limit
     * @return
     */
    @Override
    public List<MessageDTO> listMessage(String channelId, long maxCreateAt, int limit) {
        List<Message> list = DAOFactory.createMessageDAO().listMessage(channelId, maxCreateAt, limit);
        List<MessageDTO> dtoList = new ArrayList<>(list.size());
        for(Message message : list) {
            MessageDTO dto = new MessageDTO();
            BeanUtils.copyProperties(message, dto);
            dto.setSenderId(message.getSender().getId());
            dto.setSenderName(message.getSender().getName());
            dto.setSenderNickname(message.getSender().getNickname());
            dto.setSenderAvatarUrl(message.getSender().getAvatarUrl());
            dto.setSenderOnlineStatus(message.getSender().getOnlineStatus());
            dto.setSenderFirstLetterOfName(message.getSender().getFirstLetterOfName());
            if(message.getFile() != null) {
                dto.setFileId(message.getFile().getId());
                dto.setFileName(message.getFile().getName());
                dto.setFileExtension(message.getFile().getExtension());
                dto.setFileSize(message.getFile().getSize());
                dto.setFileMimeType(message.getFile().getMimeType());
                dto.setImageWidth(message.getFile().getWidth());
                dto.setImageHeight(message.getFile().getHeight());
                dto.setImageThumbWidth(message.getFile().getThumbWidth());
                dto.setImageThumbHeight(message.getFile().getThumbHeight());
                dto.setFilePath(message.getFile().getPath());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }
    
    /**
     * 根据id得到消息
     * @param id
     * @return
     */
    @Override
    public MessageDTO getById(long id) {
        Message message = DAOFactory.createMessageDAO().getById(id);
        if(message != null) {
            MessageDTO dto = new MessageDTO();
            BeanUtils.copyProperties(message, dto);
            dto.setChannelId(message.getChannel().getId());
            dto.setSenderId(message.getSender().getId());
            dto.setSenderName(message.getSender().getName());
            dto.setSenderNickname(message.getSender().getNickname());
            dto.setSenderAvatarUrl(message.getSender().getAvatarUrl());
            dto.setSenderOnlineStatus(message.getSender().getOnlineStatus());
            dto.setSenderFirstLetterOfName(message.getSender().getFirstLetterOfName());
            if(message.getFile() != null) {
                dto.setFileId(message.getFile().getId());
                dto.setFileName(message.getFile().getName());
                dto.setFileExtension(message.getFile().getExtension());
                dto.setFileSize(message.getFile().getSize());
                dto.setFileMimeType(message.getFile().getMimeType());
                dto.setImageWidth(message.getFile().getWidth());
                dto.setImageHeight(message.getFile().getHeight());
                dto.setImageThumbWidth(message.getFile().getThumbWidth());
                dto.setImageThumbHeight(message.getFile().getThumbHeight());
                dto.setFilePath(message.getFile().getPath());
            }
            return dto;
        }
        return null;
    }
    
    /**
     * 添加消息
     * @param dto
     * @return
     */
    @Override
    public MessageDTO saveMessage(MessageDTO dto) {
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        
        Channel channel = new Channel();
        channel.setId(dto.getChannelId());
        message.setChannel(channel);
        
        User sender = new User();
        sender.setId(dto.getSenderId());
        message.setSender(sender);
        if(dto.getFileId() != null && !dto.getFileId().trim().isEmpty()) {
            File file = new File();
            file.setId(dto.getFileId().trim());
            message.setFile(file);
        }
        
        long id = DAOFactory.createMessageDAO().save(message);
        if(id > 0) {
            // 更新频道的最后发送消息时间
            DAOFactory.createChannelDAO().updateLastPostAt(dto.getChannelId(), dto.getCreateAt());
            
            MessageDTO resultDTO = this.getById(id);
            if(resultDTO != null) {
                // 更新用户的未读消息数量
                Channel channelModel = DAOFactory.createChannelDAO().getById(dto.getChannelId());
                if(channelModel == null) {
                    logger.warn("The channel is not found, channel id: " + dto.getChannelId());
                } else {
                    if(channelModel.getType().equals("P")) {
                        // 私聊
                        String userId = dto.getSenderId().equals(channelModel.getFrom().getId()) ? channelModel.getTo().getId() : channelModel.getFrom().getId();
                        DAOFactory.createUnreadMessageCountDAO().increase(userId, dto.getChannelId(), (short)1);
                        
                        // 删除对方的隐藏频道
                        DAOFactory.createHideChannelDAO().remove(userId, dto.getChannelId());
                    } else {
                        // 群聊
                        DAOFactory.createUnreadMessageCountDAO().increaseGroupChannel(dto.getChannelId(), new String[]{ dto.getSenderId() }, (short)1);
                    }
                    this.publishNewMessageEvent(resultDTO, channelModel);
                }
            }
            return resultDTO;
        }
        return null;
    }
    
    /**
     * 批量添加消息
     * @param dtos
     * @return
     */
    @Override
    public int saveMessage(List<MessageDTO> dtos) {
        List<Message> messages = new ArrayList<>(dtos.size());
        for(MessageDTO dto : dtos) {
            Message message = new Message();
            BeanUtils.copyProperties(dto, message);
            
            Channel channel = new Channel();
            channel.setId(dto.getChannelId());
            message.setChannel(channel);
            
            User sender = new User();
            sender.setId(dto.getSenderId());
            message.setSender(sender);
            messages.add(message);
        }
        int total = DAOFactory.createMessageDAO().save(messages);
        DAOFactory.createChannelDAO().updateLastPostAt(dtos.get(0).getChannelId(), dtos.get(0).getCreateAt());
        // 更新用户的未读消息数量
        if (dtos.get(0).getChannelType().equals("G")) {
            DAOFactory.createUnreadMessageCountDAO().increaseGroupChannel(dtos.get(0).getChannelId(), null, (short)(total > 99 ? 99 : total));
        }
        
        Channel channelModel = DAOFactory.createChannelDAO().getById(dtos.get(0).getChannelId());
        for(MessageDTO dto : dtos) {
            this.publishNewMessageEvent(dto, channelModel);
        }
        return total;
    }
    
    /**
     * 读取消息
     * @param channelId
     * @param userId
     * @param total
     * @return
     */
    @Override
    public int readMessage(String channelId, String userId, short total) {
        UnreadMessageCountDAO dao = DAOFactory.createUnreadMessageCountDAO();
        int count = dao.decrease(userId, channelId, total);
        boolean readAll = false;
        if(count == 0) {
            count = dao.update(userId, channelId, (short)0);
            readAll = true;
        }
        NotificationEvent event = new ReadMessageEvent(userId, channelId, total, readAll);
        event.trigger();
        return count; 
    }
    
    /**
     * 删除消息
     * @param messageId
     * @param userId
     * @return
     */
    /**
     * 删除消息
     * @param messageId
     * @param senderId
     * @param channelId
     * @param toUserId
     * @return
     */
    @Override
    public int removeMessage(long messageId, String senderId, String channelId, String toUserId) {
        int count =  DAOFactory.createMessageDAO().remove(messageId, senderId);
        NotificationEvent event = new MessageRemovedEvent(messageId, senderId, channelId, toUserId);
        event.trigger();
        return count; 
    }
    
    /**
     * 添加文件
     * @param dto
     * @return
     */
    @Override
    public String saveFile(FileDTO dto) {
        File file = new File();
        BeanUtils.copyProperties(dto, file);
        return DAOFactory.createFileDAO().save(file);
    }
    
    /**
     * 发布新消息
     * @param dto
     * @param channel
     */
    private void publishNewMessageEvent(MessageDTO dto, Channel channel) {
        // 判断是私聊还是群聊
        JSONObject message = (JSONObject)JSONObject.toJSON(dto);
        if(dto.getType() != null && !dto.getType().isEmpty()) {
            message.put("type", dto.getType());
        }
        if(channel.getType().equals("P")) {
            // 私聊
            message.put("userIds", dto.getSenderId().equals(channel.getFrom().getId()) ? channel.getTo().getId() : channel.getFrom().getId());
        } else {
            message.put("groupIds", channel.getId());
        }
        NotificationEvent event = new NewMessageEvent(message);
        event.trigger();
    }

}
