package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.leo.im.model.Channel;
import org.leo.im.model.User;
import org.leo.im.model.UserChannel;
import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.UserChannelDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.support.Parameter;
import org.leo.im.store.support.ParameterDataTypeEnum;
import org.leo.im.store.util.DbUtils;

/**
 * 用户频道dao jdbc实现类
 * 
 * @author Leo
 * @date 2018/4/20
 */
public final class JdbcUserChannelDAOImpl implements BaseDAO, UserChannelDAO {
    
    private static final String USER_CHANNEL_TABLE = "im_user_channel";
    
    private static final String CHANNEL_TABLE = "im_channel";
    
    private static final String USER_TABLE = "im_user";
    
    private static final String UNREAD_MESSAGE_COUNT_TABLE = "im_unread_message_count";
    
    private static final String HIDE_CHANNEL_TABLE = "im_hide_channel";

    /**
     * 根据用户id得到用户频道列表
     * @param userId
     * @param type
     * @param limit
     * @return
     */
    @Override
    public List<UserChannel> listByUserId(String userId, String type, int limit) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT c.id,c.name,c.type,c.creator_id,uc.display_name,uc.to_user_id,u.online_status,umc.total ");
        sql.append("FROM ").append(CHANNEL_TABLE).append(" c INNER JOIN ").append(USER_CHANNEL_TABLE);
        sql.append(" uc ON c.id=uc.channel_id LEFT JOIN ").append(USER_TABLE);
        sql.append(" u ON uc.to_user_id=u.id INNER JOIN ").append(UNREAD_MESSAGE_COUNT_TABLE).append(" umc ");
        sql.append("ON umc.user_id=uc.user_id AND c.id=umc.channel_id ");
        sql.append("WHERE c.delete_at=0 AND uc.user_id=? ");
        sql.append("AND NOT EXISTS(SELECT channel_id FROM ").append(HIDE_CHANNEL_TABLE).append(" WHERE user_id=uc.user_id AND channel_id=c.id) ");
        if(type != null && !type.trim().isEmpty()) {
            sql.append(" AND c.type=? ");
        }
        sql.append("ORDER BY c.last_post_at DESC LIMIT ?");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            if(type != null && !type.trim().isEmpty()) {
                stmt.setString(2, type);
                stmt.setInt(3, limit);   
            } else {
                stmt.setInt(2, limit);
            }
            rs = stmt.executeQuery();
            List<UserChannel> list = new ArrayList<>(64);
            while(rs.next()) {
                UserChannel userChannel = new UserChannel();
                Channel channel = new Channel();
                channel.setId(rs.getString("id"));
                channel.setName(rs.getString("name"));
                channel.setType(rs.getString("type"));
                User creator = new User();
                creator.setId(rs.getString("creator_id"));
                channel.setCreator(creator);
                userChannel.setChannel(channel);
                userChannel.setDisplayName(rs.getString("display_name"));
                User toUser = new User();
                toUser.setId(rs.getString("to_user_id"));
                toUser.setOnlineStatus(rs.getString("online_status"));
                userChannel.setToUser(toUser);
                userChannel.setUnreadMessageCount(rs.getShort("total"));
                list.add(userChannel);
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 添加用户频道
     * @param userChannel
     * @return
     */
    @Override
    public int save(UserChannel userChannel) {
        List<UserChannel> list = new ArrayList<>(1);
        list.add(userChannel);
        return batchSave(list);
    }
    
    /**
     * 批量添加用户频道
     * @param userChannels
     * @return
     */
    @Override
    public int batchSave(List<UserChannel> userChannels) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }        
        String sql = "INSERT INTO " + USER_CHANNEL_TABLE + "(user_id,channel_id,display_name,to_user_id) " +
                "VALUES(?,?,?,?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for(UserChannel userChannel : userChannels) {
                stmt.setString(1, userChannel.getUser().getId());
                stmt.setString(2, userChannel.getChannel().getId());
                stmt.setString(3, userChannel.getDisplayName());
                stmt.setString(4, userChannel.getToUser() == null ? null : userChannel.getToUser().getId());
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
     * 得到用户频道
     * @param userId
     * @param channelId
     * @return
     */
    @Override
    public UserChannel get(String userId, String channelId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT c.id,c.name,c.type,c.purpose,c.member_count,uc.to_user_id,c.creator_id,uc.display_name,u.online_status,umc.total ");
        sql.append("FROM ").append(CHANNEL_TABLE).append(" c INNER JOIN ").append(USER_CHANNEL_TABLE).append(" uc ");
        sql.append("ON c.id=uc.channel_id LEFT JOIN ").append(USER_TABLE).append(" u ON uc.to_user_id=u.id ");
        sql.append("INNER JOIN ").append(UNREAD_MESSAGE_COUNT_TABLE).append(" umc ON umc.user_id=uc.user_id AND umc.channel_id=c.id ");
        sql.append("WHERE uc.user_id=? AND uc.channel_id=?");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            stmt.setString(2, channelId);
            rs = stmt.executeQuery();
            while(rs.next()) {
                UserChannel userChannel = new UserChannel();
                Channel channel = new Channel();
                channel.setId(rs.getString("id"));
                channel.setName(rs.getString("name"));
                channel.setType(rs.getString("type"));
                channel.setPurpose(rs.getString("purpose"));
                channel.setMemberCount(rs.getInt("member_count"));
                User creator = new User();
                creator.setId(rs.getString("creator_id"));
                channel.setCreator(creator);
                userChannel.setChannel(channel);
                userChannel.setDisplayName(rs.getString("display_name"));                
                User toUser = new User();
                toUser.setId(rs.getString("to_user_id"));
                toUser.setOnlineStatus(rs.getString("online_status"));
                userChannel.setToUser(toUser);
                userChannel.setUnreadMessageCount(rs.getShort("total"));
                return userChannel;
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 更新用户频道显示名
     * @param channelId
     * @param userId
     * @param displayName
     * @return
     */
    @Override
    public int updateDisplayName(String channelId, String userId, String displayName) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        List<Parameter> ps = new ArrayList<>(4);
        sql.append("UPDATE ").append(USER_CHANNEL_TABLE).append(" SET display_name=? WHERE channel_id=? AND user_id=?");
        ps.add(new Parameter("display_name", ParameterDataTypeEnum.STRING, displayName));
        ps.add(new Parameter("channel_id", ParameterDataTypeEnum.STRING, channelId));
        ps.add(new Parameter("user_id", ParameterDataTypeEnum.STRING, userId));
        
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
     * 删除用户频道
     * @param userId
     * @param channelId
     * @return
     */
    @Override
    public int remove(String userId, String channelId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(128);
        sql.append("DELETE FROM ").append(USER_CHANNEL_TABLE).append(" WHERE user_id=? AND channel_id=?");
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
     * 根据名称得到用户频道列表
     * @param userId
     * @param name
     * @param type
     * @return
     */
    public List<UserChannel> listByName(String userId, String name, String type) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT c.id,c.name,c.type,c.creator_id,uc.display_name,uc.to_user_id,u.online_status,umc.total ");
        sql.append("FROM ").append(CHANNEL_TABLE).append(" c INNER JOIN ").append(USER_CHANNEL_TABLE);
        sql.append(" uc ON c.id=uc.channel_id LEFT JOIN ").append(USER_TABLE);
        sql.append(" u ON uc.to_user_id=u.id INNER JOIN ").append(UNREAD_MESSAGE_COUNT_TABLE).append(" umc ");
        sql.append("ON umc.user_id=uc.user_id AND c.id=umc.channel_id ");
        sql.append("WHERE c.delete_at=0 AND uc.user_id=? AND (c.name LIKE ? OR uc.display_name LIKE ?)");
        if(type != null && !type.trim().isEmpty()) {
            sql.append(" AND c.type=?");
        }
        sql.append(" ORDER BY c.last_post_at DESC");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, userId);
            stmt.setString(2, "%" + name + "%");
            stmt.setString(3, "%" + name + "%");
            if(type != null && !type.trim().isEmpty()) {
                stmt.setString(4, type);
            }
            rs = stmt.executeQuery();
            List<UserChannel> list = new ArrayList<>(64);
            while(rs.next()) {
                UserChannel userChannel = new UserChannel();
                Channel channel = new Channel();
                channel.setId(rs.getString("id"));
                channel.setName(rs.getString("name"));
                channel.setType(rs.getString("type"));
                User creator = new User();
                creator.setId(rs.getString("creator_id"));
                channel.setCreator(creator);
                userChannel.setChannel(channel);
                userChannel.setDisplayName(rs.getString("display_name"));
                User toUser = new User();
                toUser.setId(rs.getString("to_user_id"));
                toUser.setOnlineStatus(rs.getString("online_status"));
                userChannel.setToUser(toUser);
                userChannel.setUnreadMessageCount(rs.getShort("total"));
                list.add(userChannel);
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }

}
