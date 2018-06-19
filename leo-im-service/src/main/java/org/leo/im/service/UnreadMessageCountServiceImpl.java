package org.leo.im.service;

import org.leo.im.api.service.UnreadMessageCountService;
import org.leo.im.store.dao.UnreadMessageCountDAO;
import org.leo.im.store.factory.DAOFactory;

/**
 * 未读消息数量
 * @author Administrator
 *
 */
public class UnreadMessageCountServiceImpl implements UnreadMessageCountService {
    
    /**
     * 批量添加未读消息数量
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int batchSaveUnreadMessageCount(String[] userIds, String channelId, short total) {
        UnreadMessageCountDAO dao = DAOFactory.createUnreadMessageCountDAO();
        return dao.batchSave(userIds, channelId, total);
    }
    
    /**
     * 更新未读消息数量
     * @param userId
     * @param channelId
     * @param quantity
     * @return
     */
    @Override
    public int updateUnreadMessageCount(String userId, String channelId, short total) {
        UnreadMessageCountDAO dao = DAOFactory.createUnreadMessageCountDAO();
        return dao.update(userId, channelId, total);
    }
    
    /**
     * 批量更新未读消息数量
     * @param userIds
     * @param channelId
     * @param quantity
     * @return
     */
    @Override
    public int batchUpdateUnreadMessageCount(String[] userIds, String channelId, short total) {
        UnreadMessageCountDAO dao = DAOFactory.createUnreadMessageCountDAO();
        return dao.batchUpdate(userIds, channelId, total);
    }
    
    /**
     * 批量增加未读消息数量
     * @param userIds
     * @param channelId
     * @param quantity
     * @return
     */
    @Override
    public int batchIncreaseUnreadMessageCount(String[] userIds, String channelId, short quantity) {
        UnreadMessageCountDAO dao = DAOFactory.createUnreadMessageCountDAO();
        return dao.batchIncrease(userIds, channelId, quantity);
    }
    
    /**
     * 增加未读消息数量
     * @param userId
     * @param channelId
     * @param quantity
     * @return
     */
    @Override
    public int increaseUnreadMessageCount(String userId, String channelId, short quantity) {
        UnreadMessageCountDAO dao = DAOFactory.createUnreadMessageCountDAO();
        return dao.increase(userId, channelId, quantity);
    }

}
