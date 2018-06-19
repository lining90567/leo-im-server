package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.leo.im.common.data.Page;
import org.leo.im.model.ChannelMember;
import org.leo.im.model.User;
import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.ChannelMemberDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.util.DbUtils;

/**
 * 频道成员dao jdbc实现类
 * 
 * @author Leo
 * @date 2018/4/11
 */
public class JdbcChannelMemberDAOImpl implements BaseDAO, ChannelMemberDAO {

    private static final String CHANNEL_MEMBER_TABLE = "im_channel_member";

    private static final String USER_TABLE = "im_user";

    /**
     * 添加成员
     * 
     * @param channelId
     * @param members
     * @return
     */
    @Override
    public int save(String channelId, List<ChannelMember> members) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }

        StringBuilder sql = new StringBuilder(256);
        sql.append("INSERT INTO ").append(CHANNEL_MEMBER_TABLE).append(" VALUES(?,?,?)");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            for (ChannelMember member : members) {
                stmt.setString(1, channelId);
                stmt.setString(2, member.getUser().getId());
                stmt.setShort(3, member.getAdmin() ? (short)1 : 0);
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
     * 得到成员列表
     * 
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    @Override
    public Page<ChannelMember> listMember(String channelId, String username, int limit, int offset) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }

        StringBuilder countSql = new StringBuilder(256);
        StringBuilder querySql = new StringBuilder(256);
        countSql.append("SELECT COUNT(*) FROM ").append(CHANNEL_MEMBER_TABLE).append(" cm INNER JOIN ");
        countSql.append(USER_TABLE).append(" u ON cm.user_id=u.id WHERE cm.channel_id=?");
        if(username != null && !username.trim().equals("")) {
            countSql.append(" AND (u.name LIKE ? OR u.nickname LIKE ?)");
        }
        
        querySql.append("SELECT u.id,u.name,u.name_first_letter,u.nickname,u.avatar_url,cm.is_admin FROM ");
        querySql.append(CHANNEL_MEMBER_TABLE).append(" cm INNER JOIN ").append(USER_TABLE).append(" u ");
        querySql.append("ON cm.user_id=u.id WHERE cm.channel_id=?");
        if(username != null && !username.trim().equals("")) {
            querySql.append(" AND (u.name LIKE ? OR u.nickname LIKE ?)");
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(countSql.toString());
            stmt.setString(1, channelId);
            if(username != null && !username.trim().equals("")) {
                stmt.setString(2, "%" + username + "%");
                stmt.setString(3, "%" + username + "%");
            }
            rs = stmt.executeQuery();
            int total = 0;
            if (rs.next()) {
                total = rs.getInt(1);
            }
            List<ChannelMember> rows = new ArrayList<>(limit);
            if (total == 0) {
                return new Page<ChannelMember>(0, rows);
            }

            stmt.close();
            stmt = conn.prepareStatement(querySql.toString());
            stmt.setString(1, channelId);
            if(username != null && !username.trim().equals("")) {
                stmt.setString(2, "%" + username + "%");
                stmt.setString(3, "%" + username + "%");
            }
            rs.close();
            rs = stmt.executeQuery();
            while (rs.next()) {
                ChannelMember member = new ChannelMember();
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setFirstLetterOfName(rs.getString("name_first_letter"));
                user.setNickname(rs.getString("nickname"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                member.setUser(user);
                member.setAdmin(rs.getBoolean("is_admin"));
                rows.add(member);
            }
            return new Page<ChannelMember>(total, rows);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 得到用户是否为频道管理员
     * @param userId
     * @param channelId
     * @return
     */
    @Override
    public boolean isAdmin(String userId, String channelId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT is_admin FROM ").append(CHANNEL_MEMBER_TABLE);
        sql.append(" WHERE user_id=? AND channel_id=?");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            stmt.setString(2, channelId);
            rs = stmt.executeQuery();
            while(rs.next()) {
                return rs.getShort("is_admin") == 1;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 移除成员
     * @param channelId
     * @param memberId
     * @return
     */
    public int removeMember(String channelId, String memberId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(128);
        sql.append("DELETE FROM ").append(CHANNEL_MEMBER_TABLE).append(" WHERE channel_id=? AND user_id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, channelId);
            stmt.setString(2, memberId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 修改管理员
     * @param channelId
     * @param memberId
     * @param isAdmin
     * @return
     */
    @Override
    public int changeAdmin(String channelId, String memberId, boolean isAdmin) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(CHANNEL_MEMBER_TABLE).append(" SET is_admin=? WHERE channel_id=? AND user_id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setShort(1, (short)(isAdmin ? 1 : 0));
            stmt.setString(2, channelId);
            stmt.setString(3, memberId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

}
