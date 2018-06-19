package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.leo.im.model.Channel;
import org.leo.im.model.File;
import org.leo.im.model.Message;
import org.leo.im.model.User;
import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.MessageDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.support.BatchSqlBuildResult;
import org.leo.im.store.support.Parameter;
import org.leo.im.store.support.ParameterDataTypeEnum;
import org.leo.im.store.support.SqlBuildResult;
import org.leo.im.store.util.DbUtils;

/**
 * 消息dao jdbc实现类
 * 
 * @author Leo
 * @date 2018/4/25
 */
public final class JdbcMessageDAOImpl implements BaseDAO, MessageDAO {

    private static final String MESSAGE_TABLE = "im_message";

    private static final String USER_TABLE = "im_user";
    
    private static final String FILE_TABLE = "im_file";

    /**
     * 得到消息列表
     * 
     * @param channelId
     * @param maxCreateTime
     * @param limit
     * @return
     */
    @Override
    public List<Message> listMessage(String channelId, long maxCreateAt, int limit) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT m.id,m.channel_id,m.sender_id,m.create_at,m.type,m.content,m.file_id,");
        sql.append("u.name,u.nickname,u.online_status,u.avatar_url,u.name_first_letter,");
        sql.append("f.name AS file_name,f.extension,f.size,f.mime_typ,f.width,f.height,f.path,f.thumb_width,f.thumb_height ");
        sql.append("FROM ").append(MESSAGE_TABLE).append(" m INNER JOIN ").append(USER_TABLE).append(" u ");
        sql.append("ON m.sender_id=u.id LEFT JOIN ").append(FILE_TABLE).append(" f ON m.file_id=f.id ");
        sql.append("WHERE m.channel_id=? AND delete_at=0");
        if (maxCreateAt > 0) {
            sql.append(" AND m.create_at<?");
        }
        sql.append(" ORDER BY m.create_at DESC LIMIT ?");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int parameterIndex = 1;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(parameterIndex, channelId);
            if (maxCreateAt > 0) {
                parameterIndex++;
                stmt.setLong(parameterIndex, maxCreateAt);
            }
            parameterIndex++;
            stmt.setInt(parameterIndex, limit);
            rs = stmt.executeQuery();
            return this.getMessageList(rs, limit);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 根据id得到消息
     * 
     * @param id
     * @return
     */
    @Override
    public Message getById(long id) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("SELECT m.id,m.channel_id,m.sender_id,m.create_at,m.type,m.content,m.file_id,");
        sql.append("u.name,u.nickname,u.online_status,u.avatar_url,name_first_letter,");
        sql.append("f.name AS file_name,f.extension,f.size,f.mime_typ,f.width,f.height,f.path,f.thumb_width,f.thumb_height ");
        sql.append("FROM ").append(MESSAGE_TABLE).append(" m INNER JOIN ").append(USER_TABLE).append(" u ");
        sql.append("ON m.sender_id=u.id LEFT JOIN ").append(FILE_TABLE).append(" f ON m.file_id=f.id ");
        sql.append("WHERE m.id=?");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            List<Message> list = this.getMessageList(rs, 1);
            return list.size() > 0 ? list.get(0) : null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 添加消息
     * 
     * @param message
     * @return
     */
    @Override
    public long save(Message message) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        long id = System.nanoTime() + ((long) (Math.random() * 9 + 1) * 10000);
        message.setId(id);
        SqlBuildResult sql = this.buildInsertSql(message);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.getSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            return stmt.executeUpdate() > 0 ? id : 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 批量添加消息
     * @param messages
     * @return
     */
    @Override
    public int save(List<Message> messages) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        for(Message message : messages) {
            long id = System.nanoTime() + ((long) (Math.random() * 9 + 1) * 10000);
            message.setId(id);
        }
        BatchSqlBuildResult sql = this.buildBatchInsertSql(messages);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.getSql());
            for(List<Parameter> ps : sql.getParameters()) {
                this.setPreparedStatmentParameters(stmt, ps);
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
     * 删除消息
     * @param messageId
     * @param userId
     * @return
     */
    @Override
    public int remove(long messageId, String userId) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        
        StringBuilder sql = new StringBuilder(256);
        sql.append("UPDATE ").append(MESSAGE_TABLE).append(" SET delete_at=? WHERE id=? AND sender_id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setLong(1, new java.util.Date().getTime());
            stmt.setLong(2, messageId);
            stmt.setString(3, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 构建insert 语句
     * 
     * @param message
     * @return
     */
    private SqlBuildResult buildInsertSql(Message message) {
        StringBuilder sql = new StringBuilder(256);
        StringBuilder fields = new StringBuilder(128);
        StringBuilder values = new StringBuilder(16);
        List<Parameter> ps = new ArrayList<>(8);
        fields.append("id,channel_id,sender_id,create_at");
        values.append("?,?,?,?");
        ps.add(new Parameter("id", ParameterDataTypeEnum.LONG, message.getId()));
        ps.add(new Parameter("channel_id", ParameterDataTypeEnum.STRING, message.getChannel().getId()));
        ps.add(new Parameter("sender_id", ParameterDataTypeEnum.STRING, message.getSender().getId()));
        ps.add(new Parameter("create_at", ParameterDataTypeEnum.LONG, message.getCreateAt()));

        if (message.getType() != null && !message.getType().trim().isEmpty()) {
            fields.append(",type");
            values.append(",?");
            ps.add(new Parameter("type", ParameterDataTypeEnum.STRING, message.getType()));
        }
        if (message.getContent() != null && !message.getContent().trim().isEmpty()) {
            fields.append(",content");
            values.append(",?");
            ps.add(new Parameter("content", ParameterDataTypeEnum.STRING, message.getContent()));
        }
        if (message.getFile() != null) {
            fields.append(",file_id");
            values.append(",?");
            ps.add(new Parameter("file_id", ParameterDataTypeEnum.STRING, message.getFile().getId()));
        }
        sql.append("INSERT INTO ").append(MESSAGE_TABLE).append("(").append(fields.toString()).append(") VALUES(");
        sql.append(values.toString()).append(")");
        return new SqlBuildResult(sql.toString(), ps);
    }
    
    /**
     * 构建批量insert语句
     * @param messages
     * @return
     */
    private BatchSqlBuildResult buildBatchInsertSql(List<Message> messages) {
        StringBuilder sql = new StringBuilder(256);
        StringBuilder fields = new StringBuilder(128);
        StringBuilder values = new StringBuilder(16);
        List<List<Parameter>> pss = new ArrayList<>(messages.size());
        fields.append("id,channel_id,sender_id,create_at");
        values.append("?,?,?,?");
        if (messages.get(0).getType() != null && !messages.get(0).getType().trim().isEmpty()) {
            fields.append(",type");
            values.append(",?");
        }
        if (messages.get(0).getContent() != null && !messages.get(0).getContent().trim().isEmpty()) {
            fields.append(",content");
            values.append(",?");
        }
        if (messages.get(0).getFile() != null) {
            fields.append(",file_id");
            values.append(",?");
        }
        sql.append("INSERT INTO ").append(MESSAGE_TABLE).append("(").append(fields.toString()).append(") VALUES(");
        sql.append(values.toString()).append(")");
        
        for(Message message : messages) {
            List<Parameter> ps = new ArrayList<>(8);
            ps.add(new Parameter("id", ParameterDataTypeEnum.LONG, message.getId()));
            ps.add(new Parameter("channel_id", ParameterDataTypeEnum.STRING, message.getChannel().getId()));
            ps.add(new Parameter("sender_id", ParameterDataTypeEnum.STRING, message.getSender().getId()));
            ps.add(new Parameter("create_at", ParameterDataTypeEnum.LONG, message.getCreateAt()));
            if (message.getType() != null && !message.getType().trim().isEmpty()) {
                ps.add(new Parameter("type", ParameterDataTypeEnum.STRING, message.getType()));
            }
            if (message.getContent() != null && !message.getContent().trim().isEmpty()) {
                ps.add(new Parameter("content", ParameterDataTypeEnum.STRING, message.getContent()));
            }
            if (message.getFile() != null) {
                ps.add(new Parameter("file_id", ParameterDataTypeEnum.STRING, message.getFile().getId()));
            }
            pss.add(ps);
        }
        return new BatchSqlBuildResult(sql.toString(), pss);
    }

    /**
     * 得到消息列表
     * 
     * @param rs
     * @param count
     * @return
     * @throws SQLException
     */
    private List<Message> getMessageList(ResultSet rs, int count) throws SQLException {
        List<Message> list = new ArrayList<>(count);
        while (rs.next()) {
            Message message = new Message();
            message.setId(rs.getLong("id"));
            message.setCreateAt(rs.getLong("create_at"));
            message.setType(rs.getString("type"));
            message.setContent(rs.getString("content"));

            if(rs.getString("file_id") != null) {
                File file = new File();
                file.setId(rs.getString("file_id"));
                file.setName(rs.getString("file_name"));
                file.setExtension(rs.getString("extension"));
                file.setSize(rs.getInt("size"));
                file.setMimeType(rs.getString("mime_typ"));
                file.setWidth(rs.getInt("width"));
                file.setHeight(rs.getInt("height"));
                file.setPath(rs.getString("path"));
                file.setThumbWidth(rs.getShort("thumb_width"));
                file.setThumbHeight(rs.getShort("thumb_height"));
                message.setFile(file);
            }

            Channel channel = new Channel();
            channel.setId(rs.getString("channel_id"));
            message.setChannel(channel);

            User sender = new User();
            sender.setId(rs.getString("sender_id"));
            sender.setName(rs.getString("name"));
            sender.setNickname(rs.getString("nickname"));
            sender.setOnlineStatus(rs.getString("online_status"));
            sender.setAvatarUrl(rs.getString("avatar_url"));
            sender.setFirstLetterOfName(rs.getString("name_first_letter"));
            message.setSender(sender);

            list.add(message);
        }
        return list;
    }

}
