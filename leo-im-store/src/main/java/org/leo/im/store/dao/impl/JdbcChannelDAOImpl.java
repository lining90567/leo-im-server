package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.leo.im.model.Channel;
import org.leo.im.model.User;
import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.ChannelDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.support.Parameter;
import org.leo.im.store.support.ParameterDataTypeEnum;
import org.leo.im.store.support.SqlBuildResult;
import org.leo.im.store.util.DbUtils;

/**
 * Channel DAO JDBC实现类
 * 
 * @author Leo
 * @date 2018/4/10
 */
public final class JdbcChannelDAOImpl implements BaseDAO, ChannelDAO {

    private static final String CHANNEL_TABLE = "im_channel";
    
    private static final String USER_TABLE = "im_user";
    
    /**
     * 得到频道列表
     * @param parameters
     * @param limit
     * @return
     */
    @Override
    public List<Channel> list(Map<String, Object> parameters, int limit) {
        return this.listChannel(parameters, null, limit);
    }
    
    /**
     * 得到群组频道
     * @param parameters
     * @param types
     * @param limit
     * @return
     */
    @Override
    public List<Channel> listGroupChannel(Map<String, Object> parameters, String[] types, int limit) {
        StringBuilder sql = new StringBuilder(32);
        if(types != null && types.length > 0) {
            for(String type : types) {
                sql.append("type='").append(type).append("' OR ");
            }
        }
        if(sql.length() > 0) {
            sql.setLength(sql.length() - 4);
            return this.listChannel(parameters, "(" + sql.toString() + ")", limit);
        }
        return this.listChannel(parameters, null, limit);
    }

    /**
     * 添加频道
     * 
     * @param channel
     * @return
     */
    @Override
    public Channel save(Channel channel) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        channel.setId(this.createId());
        SqlBuildResult sql = "G".equalsIgnoreCase(channel.getType()) ? this.buildInsertGroupChannelSql(channel) : this.buildInsertPrivateChannelSql(channel);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.getSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            if(stmt.executeUpdate() > 0) {
                return channel;
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 根据from和to得到频道
     * @param fromUserId
     * @param toUserId
     * @return
     */
    @Override
    public Channel getByFromAndTo(String fromUserId, String toUserId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT c.id,c.creator_id,c.from_user_id,c.to_user_id,c.type,");
        sql.append("u1.name AS from_user_name,u1.nickname AS from_user_nickname,");
        sql.append("u2.name AS to_user_name,u2.nickname AS to_user_nickname,u2.online_status AS to_user_online_status ");
        sql.append("FROM ").append(CHANNEL_TABLE).append(" c INNER JOIN ").append(USER_TABLE).append(" u1 ON c.from_user_id=u1.id ");
        sql.append("INNER JOIN ").append(USER_TABLE).append(" u2 ON c.to_user_id=u2.id ");
        sql.append("WHERE (c.from_user_id=? AND c.to_user_id=?) OR (c.from_user_id=? AND c.to_user_id=?)");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, fromUserId);
            stmt.setString(2, toUserId);
            stmt.setString(3, toUserId);
            stmt.setString(4, fromUserId);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Channel channel = new Channel();
                channel.setId(rs.getString("id"));
                channel.setType(rs.getString("type"));
                
                User creator = new User();
                creator.setId(rs.getString("creator_id"));
                channel.setCreator(creator);
                
                User fromUser = new User();
                fromUser.setId(rs.getString("from_user_id"));
                fromUser.setName(rs.getString("from_user_name"));
                fromUser.setNickname(rs.getString("from_user_nickname"));
                channel.setFrom(fromUser);
                
                User toUser = new User();
                toUser.setId(rs.getString("to_user_id"));
                toUser.setName(rs.getString("to_user_name"));
                toUser.setNickname(rs.getString("to_user_nickname"));
                toUser.setOnlineStatus(rs.getString("to_user_online_status"));
                channel.setTo(toUser);
                return channel;
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
            DbUtils.closeResultSet(rs);
        }
    }
    
    /**
     * 根据频道id查找频道信息
     * @param id
     * @return
     */
    @Override
    public Channel getById(String id) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT ch.id,ch.name,ch.type,ch.purpose,ch.member_count,ch.creator_id,ch.from_user_id,ch.to_user_id,");
        sql.append("u1.name AS from_user_name,u1.nickname AS from_user_nickname,u1.online_status AS from_user_online_status,");
        sql.append("u2.name AS to_user_name,u2.nickname AS to_user_nickname,u2.online_status AS to_user_online_status FROM ");
        sql.append(CHANNEL_TABLE).append(" ch LEFT JOIN im_user u1 ON ch.from_user_id=u1.id ");
        sql.append("LEFT JOIN im_user u2 ON ch.to_user_id=u2.id");
        sql.append(" WHERE ch.id=?");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Channel channel = new Channel();
                channel.setId(id);
                channel.setName(rs.getString("name"));
                channel.setType(rs.getString("type"));
                channel.setPurpose(rs.getString("purpose"));
                channel.setMemberCount(rs.getInt("member_count"));
                
                User creator = new User();
                creator.setId(rs.getString("creator_id"));
                channel.setCreator(creator);
                
                if(rs.getString("from_user_id") != null) {
                    User fromUser = new User();
                    fromUser.setId(rs.getString("from_user_id"));
                    fromUser.setName(rs.getString("from_user_name"));
                    fromUser.setNickname(rs.getString("from_user_nickname"));
                    fromUser.setOnlineStatus(rs.getString("from_user_online_status"));
                    channel.setFrom(fromUser);
                }
                
                if(rs.getString("to_user_id") != null) {
                    User toUser = new User();
                    toUser.setId(rs.getString("to_user_id"));
                    toUser.setName(rs.getString("to_user_name"));
                    toUser.setNickname(rs.getString("to_user_nickname"));
                    toUser.setOnlineStatus(rs.getString("to_user_online_status"));
                    channel.setTo(toUser);
                }
                return channel;
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
            DbUtils.closeResultSet(rs);
        }
    }
    
    /**
     * 更新最后发送消息的时间
     * @param id
     * @param lastPostAt
     * @return
     */
    public int updateLastPostAt(String id, long lastPostAt) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(CHANNEL_TABLE).append(" SET last_post_at=? WHERE id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setLong(1, lastPostAt);
            stmt.setString(2, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 增加成员数量
     * @param id
     * @param count
     * @return
     */
    public int increaseMemberCount(String id, int count) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(CHANNEL_TABLE).append(" SET member_count=member_count+? WHERE id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setInt(1, count);
            stmt.setString(2, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 更新字符字段
     * @param id
     * @param field
     * @param value
     * @return
     */
    @Override
    public int updateStringField(String id, String field, String value) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(CHANNEL_TABLE).append(" SET ");
        sql.append(field).append("=? WHERE id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, value);
            stmt.setString(2, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 更新整型字段
     * @param id
     * @param field
     * @param value
     * @return
     */
    @Override
    public int updateIntegerField(String id, String field, int value) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(CHANNEL_TABLE).append(" SET ");
        sql.append(field).append("=? WHERE id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setInt(1, value);
            stmt.setString(2, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 删除频道
     * @param id
     * @return
     */
    @Override
    public int remove(String id) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        long deleteAt = new java.util.Date().getTime();
        sql.append("UPDATE ").append(CHANNEL_TABLE).append(" SET delete_at=? WHERE id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setLong(1, deleteAt);
            stmt.setString(2, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 得到频道列表
     * @param parameters
     * @param otherCondition
     * @param limit
     * @return
     */
    private List<Channel> listChannel(Map<String, Object> parameters, String otherCondition, int limit) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        
        SqlBuildResult sql = this.buildQuerySql(parameters, otherCondition, limit);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.getSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            rs = stmt.executeQuery();
            List<Channel> list = new ArrayList<Channel>(64);
            while (rs.next()) {
                Channel channel = new Channel();
                channel.setId(rs.getString("id"));
                channel.setName(rs.getString("name"));
                channel.setType(rs.getString("type"));
                if(rs.getString("from_user_id") != null) {
                    User from = new User();
                    from.setId(rs.getString("from_user_id"));
                    channel.setFrom(from);
                }
                if(rs.getString("to_user_id") != null) {
                    User to = new User();
                    to.setId(rs.getString("to_user_id"));
                    channel.setTo(to);
                }
                list.add(channel);
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
     * 构建查询语句
     * @param parameters
     * @param otherCondition
     * @param limit
     * @return
     */
    private SqlBuildResult buildQuerySql(Map<String, Object> parameters, String otherCondition, int limit) {
        StringBuilder sql = new StringBuilder(256);
        StringBuilder whereSql = new StringBuilder(128);
        sql.append("SELECT id,name,type,from_user_id,to_user_id FROM ").append(CHANNEL_TABLE);
        List<Parameter> parameterList = new ArrayList<>(64);
        if(!parameters.isEmpty()) {
            Set<Entry<String, Object>> entrySet = parameters.entrySet();
            for(Entry<String, Object> entry : entrySet) {
                Parameter param = getParameter(entry.getKey(), entry.getValue());
                if(param != null) {
                    parameterList.add(param);
                    whereSql.append(param.getName()).append("=? AND ");
                }
            }
        }
        if(whereSql.length() > 0) {
            whereSql.setLength(whereSql.length() - 5);
            sql.append(" WHERE ").append(whereSql.toString());
        }
        if(otherCondition != null && !otherCondition.trim().isEmpty()) {
            sql.append(whereSql.length() > 0 ? " AND " : " WHERE ");
            sql.append(otherCondition);
        }
        sql.append(" ORDER BY last_post_at");
        if(limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }
        return new SqlBuildResult(sql.toString(), parameterList);
    }
    
    /**
     * 得到查询参数
     * @param name
     * @param value
     * @return
     */
    private Parameter getParameter(String name, Object value) {
        if(name.equals("deleteAt")) {
            return new Parameter("delete_at", ParameterDataTypeEnum.LONG, Long.parseLong(value.toString()));
        }
        if(name.equals("creatorId")) {
            return new Parameter("creator_id", ParameterDataTypeEnum.STRING, value.toString());
        }
        if(name.equals("type")) {
            return new Parameter("type", ParameterDataTypeEnum.STRING, value.toString());
        }  
        return null;
    }
    
    /**
     * 构建插入群聊频道语句
     * @param channel
     * @return
     */
    private SqlBuildResult buildInsertGroupChannelSql(Channel channel) {
        StringBuilder sql = new StringBuilder(256);
        StringBuilder fields = new StringBuilder(128);
        StringBuilder values = new StringBuilder(32);
        List<Parameter> parameters = new ArrayList<>(16);
        sql.append("INSERT INTO ").append(CHANNEL_TABLE);
        fields.append("id,name,type,create_at,member_count,creator_id");
        values.append("?,?,?,?,?,?");
        parameters.add(new Parameter("id", ParameterDataTypeEnum.STRING, channel.getId()));
        parameters.add(new Parameter("name", ParameterDataTypeEnum.STRING, channel.getName()));
        parameters.add(new Parameter("type", ParameterDataTypeEnum.STRING, channel.getType()));
        parameters.add(new Parameter("create_at", ParameterDataTypeEnum.LONG, System.currentTimeMillis()));
        parameters.add(new Parameter("member_count", ParameterDataTypeEnum.INT, channel.getMemberCount()));
        parameters.add(new Parameter("creator_id", ParameterDataTypeEnum.STRING, channel.getCreator().getId()));
        if(channel.getPurpose() != null && !channel.getPurpose().trim().isEmpty()) {
            fields.append(",purpose");
            values.append(",?");
            parameters.add(new Parameter("purpose", ParameterDataTypeEnum.STRING, channel.getPurpose().trim()));
        }
        if(channel.getFrom() != null && channel.getFrom().getId() != null && !channel.getFrom().getId().trim().isEmpty()) {
            fields.append(",from_user_id");
            values.append(",?");
            parameters.add(new Parameter("from_user_id", ParameterDataTypeEnum.STRING, channel.getFrom().getId().trim()));
        }
        if(channel.getTo() != null && channel.getTo().getId() != null && !channel.getTo().getId().trim().isEmpty()) {
            fields.append(",to_user_id");
            values.append(",?");
            parameters.add(new Parameter("to_user_id", ParameterDataTypeEnum.STRING, channel.getTo().getId().trim()));
        }
        sql.append("(").append(fields.toString()).append(")").append(" VALUES(").append(values.toString()).append(")");
        return new SqlBuildResult(sql.toString(), parameters);
    }
    
    /**
     * 构建插入私聊频道语句
     * @param channel
     * @return
     */
    private SqlBuildResult buildInsertPrivateChannelSql(Channel channel) {
        StringBuilder sql = new StringBuilder(256);
        StringBuilder fields = new StringBuilder(128);
        StringBuilder values = new StringBuilder(32);
        List<Parameter> parameters = new ArrayList<>(16);
        sql.append("INSERT INTO ").append(CHANNEL_TABLE);
        fields.append("id,name,type,create_at,member_count,creator_id,from_user_id,to_user_id");
        values.append(" SELECT ?,?,?,?,?,?,?,?");
        parameters.add(new Parameter("id", ParameterDataTypeEnum.STRING, channel.getId()));
        parameters.add(new Parameter("name", ParameterDataTypeEnum.STRING, channel.getName()));
        parameters.add(new Parameter("type", ParameterDataTypeEnum.STRING, channel.getType()));
        parameters.add(new Parameter("create_at", ParameterDataTypeEnum.LONG, System.currentTimeMillis()));
        parameters.add(new Parameter("member_count", ParameterDataTypeEnum.INT, channel.getMembers().size()));
        parameters.add(new Parameter("creator_id", ParameterDataTypeEnum.STRING, channel.getCreator().getId()));
        parameters.add(new Parameter("from_user_id", ParameterDataTypeEnum.STRING, channel.getFrom().getId().trim()));
        parameters.add(new Parameter("to_user_id", ParameterDataTypeEnum.STRING, channel.getTo().getId().trim()));
        if(channel.getPurpose() != null && !channel.getPurpose().trim().isEmpty()) {
            fields.append(",purpose");
            values.append(",?");
            parameters.add(new Parameter("purpose", ParameterDataTypeEnum.STRING, channel.getPurpose().trim()));
        }
        values.append(" FROM dual ").append(" WHERE NOT EXISTS(SELECT id FROM ").append(CHANNEL_TABLE);
        values.append(" WHERE (from_user_id=? AND to_user_id=?) OR (from_user_id=? AND to_user_id=?))");
        parameters.add(new Parameter("from_user1", ParameterDataTypeEnum.STRING, channel.getFrom().getId().trim()));
        parameters.add(new Parameter("to_user1", ParameterDataTypeEnum.STRING, channel.getTo().getId().trim()));
        parameters.add(new Parameter("from_user2", ParameterDataTypeEnum.STRING, channel.getTo().getId().trim()));
        parameters.add(new Parameter("to_user2", ParameterDataTypeEnum.STRING, channel.getFrom().getId().trim()));
        sql.append("(").append(fields.toString()).append(")").append(values.toString());
        return new SqlBuildResult(sql.toString(), parameters);
    }

}
