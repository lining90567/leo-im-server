package org.leo.im.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.leo.im.api.exception.ServiceException;
import org.leo.im.api.service.UserService;
import org.leo.im.common.data.Page;
import org.leo.im.api.dto.UserDTO;
import org.leo.im.model.User;
import org.leo.im.notification.event.AvatarChangedEvent;
import org.leo.im.notification.event.NicknameChangedEvent;
import org.leo.im.notification.event.NotificationEvent;
import org.leo.im.notification.event.OnlineStatusChangedEvent;
import org.leo.im.service.util.PasswordUtils;
import org.leo.im.store.dao.UserDAO;
import org.leo.im.store.factory.DAOFactory;
import org.leo.im.util.BeanUtils;

/**
 * 用户服务实现类
 * 
 * @author Leo
 * @date 2018/4/9
 */
public final class UserServiceImpl implements UserService {
    
    /**
     * 验证用户登录
     * @param name
     * @param password
     * @return
     */
    @Override
    public UserDTO verifyLogin(String name, String password) {
        User user = DAOFactory.createUserDAO().getByName(name);
        if(user == null) {
            return null;
        }
        String md5Password = null;
        try {
            md5Password = PasswordUtils.getMd5Password(password, user.getSalt());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ServiceException(e);
        }
        if(md5Password != null && md5Password.equals(user.getPassword())) {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        }
        return null;
    }
    
    /**
     * 根据id得到用户
     * @param id
     * @return
     */
    @Override
    public UserDTO getById(String id) {
        UserDTO dto = new UserDTO();
        User user = DAOFactory.createUserDAO().getById(id);
        if(user != null) {
            BeanUtils.copyProperties(user, dto);
        }
        return dto;
    }
    
    /**
     * 添加用户
     * @param dto
     * @return
     */
    @Override
    public String saveUser(UserDTO dto) {
        UserDAO dao = DAOFactory.createUserDAO();
        if(dao.usernameExists(null, dto.getName())) {
            throw new ServiceException("用户 " + dto.getName() + " 已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        
        String salt = PasswordUtils.generateSalt();
        user.setSalt(salt);
        
        String md5Password = null;
        try {
            md5Password = PasswordUtils.getMd5Password(user.getPassword(), salt);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new ServiceException(e);
        }
        user.setPassword(md5Password);
        return dao.save(user);
    }
    
    /**
     * 根据名称或昵称分页查询用户
     * @param name
     * @param limit
     * @param offset
     * @return
     */
    @Override
    public Page<UserDTO> listByNameOrNickname(String name, int limit, int offset) {
        Page<User> userResult = DAOFactory.createUserDAO().listByNameOrNickname(name, limit, offset);
        List<UserDTO> dtoList = new ArrayList<>(userResult.getRows().size());
        for(User user : userResult.getRows()) {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            dtoList.add(dto);
        }
        return new Page<UserDTO>(userResult.getTotal(), dtoList);
    }
    
    /**
     * 更新用户
     * @param dto
     * @param updateNullValueField
     * @return
     */
    @Override
    public UserDTO updateUser(UserDTO dto, boolean updateNullValueField) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        if(dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            String salt = PasswordUtils.generateSalt();
            user.setSalt(salt);
            
            String md5Password = null;
            try {
                md5Password = PasswordUtils.getMd5Password(user.getPassword(), salt);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                throw new ServiceException(e);
            }
            user.setPassword(md5Password);
        }
        UserDAO dao = DAOFactory.createUserDAO();
        if(dao.update(user, updateNullValueField) == 1) {
            publishEvent(dto);
            User returnUser = dao.getById(dto.getId());
            UserDTO returnDTO = new UserDTO();
            BeanUtils.copyProperties(returnUser, returnDTO);
            return returnDTO;
        }
        return null;
    }
    
    /**
     * 
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    public Page<UserDTO> listNonMembers(String channelId, String username, int limit, int offset) {
        Page<User> pagedUser = DAOFactory.createUserDAO().listNonMembers(channelId, username, limit, offset);
        if(pagedUser.getTotal() == 0) {
            return new Page<UserDTO>(0, new ArrayList<UserDTO>(0));
        }
        List<UserDTO> list = new ArrayList<>(pagedUser.getRows().size());
        for(User user : pagedUser.getRows()) {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setNickname(user.getNickname());
            dto.setFirstLetterOfName(user.getFirstLetterOfName());
            dto.setAvatarUrl(user.getAvatarUrl());
            list.add(dto);
        }
        return new Page<UserDTO>(pagedUser.getTotal(), list);
    }
    
    /**
     * 批量下线用户
     * @param userIds
     */
    @Override
    public int batchOffline(Set<String> userIds) {
        return DAOFactory.createUserDAO().offline(userIds);
    }
    
    /**
     * 修改用户口令
     * @param userId
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public int updatePassword(String userId, String username, String oldPassword, String newPassword) {
        if(this.verifyLogin(username, oldPassword) != null) {
            User user = new User();
            // 更新口令
            String salt = PasswordUtils.generateSalt();
            try {
                String newMd5Password = PasswordUtils.getMd5Password(newPassword, salt);
                user.setId(userId);
                user.setSalt(salt);
                user.setPassword(newMd5Password);
                return DAOFactory.createUserDAO().update(user, false);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * 发布消息
     * @param dto
     */
    private void publishEvent(UserDTO dto) {
        if(dto.getOnlineStatus() != null && !dto.getOnlineStatus().trim().isEmpty()) {
            NotificationEvent event = new OnlineStatusChangedEvent(dto.getId(), dto.getOnlineStatus());
            event.trigger();
        }
        if(dto.getNickname() != null && !dto.getNickname().trim().isEmpty()) {
            NotificationEvent event = new NicknameChangedEvent(dto.getId(), dto.getNickname());
            event.trigger();
        }
        if(dto.getAvatarUrl() != null && !dto.getAvatarUrl().trim().isEmpty()) {
            NotificationEvent event = new AvatarChangedEvent(dto.getId(), dto.getAvatarUrl());
            event.trigger(); 
        }
    }

}
