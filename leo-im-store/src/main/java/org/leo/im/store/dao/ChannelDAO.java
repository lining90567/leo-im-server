package org.leo.im.store.dao;

import java.util.List;
import java.util.Map;

import org.leo.im.model.Channel;

/**
 * 频道dao接口
 * 
 * @author Leo
 * @date 2018/4/10
 */
public interface ChannelDAO {
    
    /**
     * 得到频道列表
     * @param parameters
     * @param limit
     * @return
     */
    List<Channel> list(Map<String, Object> parameters, int limit);
    
    /**
     * 得到群组频道
     * @param parameters
     * @param types
     * @param limit
     * @return
     */
    List<Channel> listGroupChannel(Map<String, Object> parameters, String[] types, int limit);
    
    /**
     * 添加频道
     * @param channel
     * @return
     */
    Channel save(Channel channel);
    
    /**
     * 根据from和to得到频道
     * @param fromUserId
     * @param toUserId
     * @return
     */
    Channel getByFromAndTo(String fromUserId, String toUserId);
    
    /**
     * 根据频道id查找频道信息
     * @param id
     * @return
     */
    Channel getById(String id);
    
    /**
     * 更新最后发送消息的时间
     * @param id
     * @param lastPostAt
     * @return
     */
    int updateLastPostAt(String id, long lastPostAt);
    
    /**
     * 增加成员数量
     * @param id
     * @param count
     * @return
     */
    int increaseMemberCount(String id, int count);
    
    /**
     * 更新字符字段
     * @param id
     * @param field
     * @param value
     * @return
     */
    int updateStringField(String id, String field, String value);
    
    /**
     * 更新整型字段
     * @param id
     * @param field
     * @param value
     * @return
     */
    int updateIntegerField(String id, String field, int value);
    
    /**
     * 删除频道
     * @param id
     * @return
     */
    int remove(String id);

}
