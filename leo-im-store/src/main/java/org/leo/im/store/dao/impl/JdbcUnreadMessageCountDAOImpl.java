package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.UnreadMessageCountDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.support.Parameter;
import org.leo.im.store.support.ParameterDataTypeEnum;
import org.leo.im.store.util.DbUtils;

/**
 * 未读消息数量dao jdbc实现类
 * 
 * @author Leo
 * @date 2018/4/26
 */
public final class JdbcUnreadMessageCountDAOImpl implements BaseDAO, UnreadMessageCountDAO {
    
    private static final String UNREAD_MESSAGE_COUNT_TABLE = "im_unread_message_count";

    /**
     * 添加未读消息
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int save(String userId, String channelId, short total) {
        return this.batchSave(new String[] { userId }, channelId, total);
    }
    
    /**
     * 批量添加未读消息
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int batchSave(String[] userIds, String channelId, short total) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        String sql = "INSERT INTO " + UNREAD_MESSAGE_COUNT_TABLE + "(user_id,channel_id,total) VALUES(?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for(String userId : userIds) {
                stmt.setString(1, userId);
                stmt.setString(2, channelId);
                stmt.setShort(3, total);
                stmt.addBatch();
            }
            return stmt.executeBatch().length;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 更新未读消息数量
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int update(String userId, String channelId, short total) {
        return batchUpdate(new String[]{ userId }, channelId, total);
    }

    /**
     * 批量更新未读消息数量
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int batchUpdate(String[] userIds, String channelId, short total) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        String sql = "UPDATE " + UNREAD_MESSAGE_COUNT_TABLE + " SET total=? WHERE user_id=? AND channel_id=? AND total<99";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for(String userId : userIds) {
                stmt.setShort(1, total);
                stmt.setString(2, userId);
                stmt.setString(3, channelId);
                stmt.addBatch();
            }
            return stmt.executeBatch().length;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 增加未读消息数
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int increase(String userId, String channelId, short total) {
        return this.batchIncrease(new String[] { userId }, channelId, total);
    }
    
    /**
     * 批量增加未读消息数
     * @param userIds
     * @param channelId
     * @param total
     * @return
     */
    @Override
    public int batchIncrease(String[] userIds, String channelId, short total) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        String sql = "UPDATE " + UNREAD_MESSAGE_COUNT_TABLE + " SET total=total+? WHERE user_id=? AND channel_id=? AND total<99";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for(String userId : userIds) {
                stmt.setShort(1, total);
                stmt.setString(2, userId);
                stmt.setString(3, channelId);
                stmt.addBatch();
            }
            return stmt.executeBatch().length;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 增加群组频道的未读消息数
     * @param channelId
     * @param exceptiveUserIds
     * @param total
     * @return
     */
    @Override
    public int increaseGroupChannel(String channelId, String[] exceptiveUserIds, short total) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        List<Parameter> ps = new ArrayList<>(8);
        sql.append("UPDATE ").append(UNREAD_MESSAGE_COUNT_TABLE).append(" SET total=total+? WHERE channel_id=? AND total<99");
        ps.add(new Parameter("total", ParameterDataTypeEnum.SHORT, total));
        ps.add(new Parameter("channel_id", ParameterDataTypeEnum.STRING, channelId));
        
        StringBuilder notInBuilder = new StringBuilder(32); 
        if(exceptiveUserIds != null && exceptiveUserIds.length > 0) {
            for(int i = 0; i < exceptiveUserIds.length; i++) {
                notInBuilder.append(notInBuilder.length() > 0 ? "," : "").append("?");
                ps.add(new Parameter("exceptive_user_id" + i, ParameterDataTypeEnum.STRING, exceptiveUserIds[i]));
            }
            sql.append(" AND user_id NOT IN(").append(notInBuilder.toString()).append(")");
        }
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            this.setPreparedStatmentParameters(stmt, ps);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 减少未读消息数量
     * @param userId
     * @param channelId
     * @param total
     * @return
     */
    public int decrease(String userId, String channelId, short total) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(UNREAD_MESSAGE_COUNT_TABLE).append(" SET total=total-? ");
        sql.append("WHERE channel_id=? AND user_id=? AND total>=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, channelId);
            stmt.setString(2, userId);
            stmt.setShort(3, total);
            stmt.setShort(4, total);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 删除未读消息数量
     * @param channelId
     * @param userId
     * @return
     */
    @Override
    public int remove(String channelId, String userId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(128);
        sql.append("DELETE FROM ").append(UNREAD_MESSAGE_COUNT_TABLE).append(" WHERE channel_id=? AND user_id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, channelId);
            stmt.setString(2, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

}
