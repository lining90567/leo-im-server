package org.leo.im.store.dao;

import java.util.Set;

import org.leo.im.common.data.Page;
import org.leo.im.model.User;

/**
 * 用户管理dao接口
 * 
 * @author Leo
 * @date 2018/4/8
 */
public interface UserDAO {

    /**
     * 添加用户
     * @param user
     * @return 用户id
     */
    String save(User user);
    
    /**
     * 根据用户名得到用户
     * @param name
     * @return
     */
    User getByName(String name);
    
    /**
     * 根据id得到用户
     * @param id
     * @return
     */
    User getById(String id);
    
    /**
     * 根据名称或昵称得到用户列表
     * @param name
     * @param limit
     * @param offset
     * @return
     */
    Page<User> listByNameOrNickname(String name, int limit, int offset);
    
    /**
     * 更新用户
     * @param user
     * @return
     */
    int update(User user);
    
    /**
     * 更新用户
     * @param user
     * @param updateNullValueField
     * @return
     */
    int update(User user, boolean updateNullValueField);
    
    /**
     * 判断用户名是否已经存在
     * @param userId
     * @param username
     * @return
     */
    boolean usernameExists(String userId, String username);
    
    /**
     * 分页查询非组成员
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    Page<User> listNonMembers(String channelId, String username, int limit, int offset);
    
    /**
     * 批量设置用户下线
     * @param userIds
     * @return
     */
    int offline(Set<String> userIds);
    
}
