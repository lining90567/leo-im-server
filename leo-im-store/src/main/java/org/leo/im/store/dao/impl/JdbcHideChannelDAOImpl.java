package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.HideChannelDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.util.DbUtils;

public class JdbcHideChannelDAOImpl implements BaseDAO, HideChannelDAO {
    
    private static final String HIDE_CHANNEL_TABLE = "im_hide_channel";

    /**
     * 添加隐藏频道
     * @param userId
     * @param channelId
     */
    @Override
    public int save(String userId, String channelId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(64);
        sql.append("INSERT INTO ").append(HIDE_CHANNEL_TABLE).append("(user_id,channel_id) VALUES(?,?)");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            stmt.setString(2, channelId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 删除隐藏频道
     * @param userId
     * @param channelId
     */
    @Override
    public int remove(String userId, String channelId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(64);
        sql.append("DELETE FROM ").append(HIDE_CHANNEL_TABLE).append(" WHERE user_id=? AND channel_id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            stmt.setString(2, channelId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

}
