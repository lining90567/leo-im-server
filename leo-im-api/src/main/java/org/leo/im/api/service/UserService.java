package org.leo.im.api.service;

import java.util.Set;

import org.leo.im.api.annotation.Transactional;
import org.leo.im.api.dto.UserDTO;
import org.leo.im.common.data.Page;

/**
 * 用户服务接口
 * 
 * @author Leo
 * @date 2018/4/9
 */
public interface UserService {

    /**
     * 验证用户登录
     * 
     * @param loginName
     * @param password
     * @return
     */
    UserDTO verifyLogin(String loginName, String password);

    /**
     * 根据id得到用户
     * 
     * @param id
     * @return
     */
    UserDTO getById(String id);

    /**
     * 添加用户
     * 
     * @param dto
     * @return
     */
    @Transactional
    String saveUser(UserDTO dto);

    /**
     * 根据名称或昵称分页查询用户
     * 
     * @param name
     * @param limit
     * @param offset
     * @return
     */
    Page<UserDTO> listByNameOrNickname(String name, int limit, int offset);
    
    /**
     * 更新用户
     * @param dto
     * @param updateNullValueField
     * @return
     */
    @Transactional
    UserDTO updateUser(UserDTO dto, boolean updateNullValueField);
    
    /**
     * 
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    Page<UserDTO> listNonMembers(String channelId, String username, int limit, int offset);
    
    /**
     * 批量下线用户
     * @param userIds
     * @return
     */
    @Transactional
    int batchOffline(Set<String> userIds);
    
    /**
     * 修改用户口令
     * @param userId
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Transactional
    int updatePassword(String userId, String username, String oldPassword, String newPassword);

}
