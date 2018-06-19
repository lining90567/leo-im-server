package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.leo.im.common.data.Page;
import org.leo.im.model.User;
import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.UserDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.support.Parameter;
import org.leo.im.store.support.ParameterDataTypeEnum;
import org.leo.im.store.support.SqlBuildResult;
import org.leo.im.store.util.DbUtils;
import org.leo.im.store.util.FirstLetterUtil;

/**
 * 用户dao jdbc实现类
 * 
 * @author Leo
 * @date 2018/4/10
 */
public class JdbcUserDAOImpl implements BaseDAO, UserDAO {

    private final static String USER_TABLE = "im_user";
    
    private final static String CHANNEL_MEMBER_TABLE = "im_channel_member";

    /**
     * 添加用户
     * 
     * @param user
     * @return 用户id
     */
    @Override
    public String save(User user) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }

        SqlBuildResult sql = this.buildInsertSql(user);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.getSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            if (stmt.executeUpdate() > 0) {
                return user.getId();
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

    /**
     * 根据用户名得到用户
     * 
     * @param name
     * @return
     */
    @Override
    public User getByName(String name) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        String sql = "SELECT id,nickname,salt,password,name_first_letter,avatar_url FROM " + 
                USER_TABLE + " WHERE name=? AND locked=0";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(name);
                user.setNickname(rs.getString("nickname"));
                user.setSalt(rs.getString("salt"));
                user.setPassword(rs.getString("password"));
                user.setFirstLetterOfName(rs.getString("name_first_letter"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                return user;
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
     * 根据id得到用户
     * 
     * @param id
     * @return
     */
    @Override
    public User getById(String id) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        String sql = "SELECT name,name_first_letter,nickname,avatar_url,online_status FROM " + USER_TABLE + " WHERE id=? AND locked=0";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(id);
                user.setName(rs.getString("name"));
                user.setFirstLetterOfName(rs.getString("name_first_letter"));
                user.setNickname(rs.getString("nickname"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setOnlineStatus(rs.getString("online_status"));
                return user;
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
     * 根据名称或昵称得到用户列表
     * @param name
     * @param limit
     * @param offset
     * @return
     */
    @Override
    public Page<User> listByNameOrNickname(String name, int limit, int offset) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        SqlBuildResult sql = this.buildPagingQueryResult(name, limit, offset);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.getCountSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            rs = stmt.executeQuery();
            int total = 0;
            if(rs.next()) {
                total = rs.getInt(1);
            }
            List<User> rows = new ArrayList<>(limit);
            if(total == 0) {
                return new Page<User>(total, rows);
            }
            
            stmt.close();
            stmt = conn.prepareStatement(sql.getSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            rs.close();
            rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setFirstLetterOfName(rs.getString("name_first_letter"));
                user.setNickname(rs.getString("nickname"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                rows.add(user);
            }
            return new Page<User>(total, rows);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 更新用户
     * @param user
     * @return
     */
    @Override
    public int update(User user) {
        return update(user, true);
    }
    
    /**
     * 更新用户
     * @param user
     * @param updateNullValueField
     * @return
     */
    @Override
    public int update(User user, boolean updateNullValueField) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        SqlBuildResult sql = this.buildUpdateSql(user, updateNullValueField);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.getSql());
            this.setPreparedStatmentParameters(stmt, sql.getParameters());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 判断用户名是否已经存在
     * @param userId
     * @param username
     * @return
     */
    @Override
    public boolean usernameExists(String userId, String username) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(128);
        sql.append("SELECT id FROM ").append(USER_TABLE).append(" WHERE name=?");
        if(userId != null && !userId.trim().isEmpty()) {
            sql.append(" AND id<>?");
        }
        sql.append(" LIMIT 1");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            stmt.setString(1, username);
            if(userId != null && !userId.trim().isEmpty()) {
                stmt.setString(2, userId);
            }
            rs = stmt.executeQuery();
            if(rs != null) {
                while(rs.next()) {
                    return true;
                }
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
     * 分页查询非组成员
     * @param channelId
     * @param username
     * @param limit
     * @param offset
     * @return
     */
    @Override
    public Page<User> listNonMembers(String channelId, String username, int limit, int offset) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        
        StringBuilder countSql = new StringBuilder(256);
        countSql.append("SELECT COUNT(*) FROM ").append(USER_TABLE).append(" u ");
        countSql.append("WHERE NOT EXISTS(SELECT user_id FROM ").append(CHANNEL_MEMBER_TABLE).append(" WHERE user_id=u.id AND channel_id=?)");
        countSql.append(" AND u.id<>'00000000000000000000000000000000'");
        if(username != null && !username.trim().equals("")) {
            countSql.append(" AND (u.name LIKE ? OR u.nickname LIKE ?)");
        }
        
        StringBuilder querySql = new StringBuilder(256);
        querySql.append("SELECT u.id,u.name,u.name_first_letter,u.nickname,u.avatar_url ");
        querySql.append("FROM im_user u ");
        querySql.append("WHERE NOT EXISTS(SELECT user_id FROM ").append(CHANNEL_MEMBER_TABLE).append(" WHERE user_id=u.id AND channel_id=?)");
        querySql.append(" AND u.id<>'00000000000000000000000000000000'");
        if(username != null && !username.trim().equals("")) {
            querySql.append(" AND (u.name LIKE ? OR u.nickname LIKE ?)");
        }
        querySql.append(" LIMIT ? OFFSET ?");
        
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
            List<User> rows = new ArrayList<>(limit);
            if (total == 0) {
                return new Page<User>(0, rows);
            }

            stmt.close();
            stmt = conn.prepareStatement(querySql.toString());
            int position = 1;
            stmt.setString(position, channelId);
            if(username != null && !username.trim().equals("")) {
                stmt.setString(2, "%" + username + "%");
                stmt.setString(3, "%" + username + "%");
                position += 2;
            }
            position++;
            stmt.setInt(position, limit);
            position++;
            stmt.setInt(position, offset);
            rs.close();
            rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setFirstLetterOfName(rs.getString("name_first_letter"));
                user.setNickname(rs.getString("nickname"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                rows.add(user);
            }
            return new Page<User>(total, rows);
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeResultSet(rs);
            DbUtils.closeStatement(stmt);
        }
    }
    
    /**
     * 批量设置用户下线
     * @param userIds
     * @return
     */
    @Override
    public int offline(Set<String> userIds) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(128);
        sql.append("UPDATE ").append(USER_TABLE).append(" SET online_status=? WHERE id=?");
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            for(String userId : userIds) {
                stmt.setString(1, "offline");
                stmt.setString(2, userId);
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
     * 构建insert语句
     * 
     * @param user
     * @return
     */
    private SqlBuildResult buildInsertSql(User user) {
        String id = this.createId();
        user.setId(id);
        StringBuilder sql = new StringBuilder(256);
        StringBuilder values = new StringBuilder(64);
        sql.append("INSERT INTO ").append(USER_TABLE).append("(id,name,salt,password,created_at,name_first_letter");
        values.append("?,?,?,?,?,?");
        List<Parameter> ps = new ArrayList<Parameter>();
        ps.add(new Parameter("id", ParameterDataTypeEnum.STRING, id));
        ps.add(new Parameter("name", ParameterDataTypeEnum.STRING, user.getName()));
        ps.add(new Parameter("salt", ParameterDataTypeEnum.STRING, user.getSalt()));
        ps.add(new Parameter("password", ParameterDataTypeEnum.STRING, user.getPassword()));
        ps.add(new Parameter("created_at", ParameterDataTypeEnum.DATETIME, new Date()));
        String firstLetterOfName = FirstLetterUtil.getFirstLetter(user.getNickname() != null && !user.getNickname().trim().isEmpty() ?
                user.getNickname().trim() : user.getName().trim());
        ps.add(new Parameter("name_first_letter", ParameterDataTypeEnum.STRING, firstLetterOfName));
        if (user.getNickname() != null && !user.getNickname().trim().isEmpty()) {
            sql.append(",nickname");
            values.append(",?");
            ps.add(new Parameter("nickname", ParameterDataTypeEnum.STRING, user.getNickname().trim()));
        }
        sql.append(") VALUES(").append(values.toString()).append(")");
        return new SqlBuildResult(sql.toString(), ps);
    }
    
    /**
     * 构建分页查询语句
     * @param name
     * @param limit
     * @param offset
     * @return
     */
    private SqlBuildResult buildPagingQueryResult(String name, int limit, int offset) {
        StringBuilder countSql = new StringBuilder(256);
        StringBuilder querySql = new StringBuilder(256);
        StringBuilder whereSql = new StringBuilder(64);
        List<Parameter> ps = new ArrayList<>(3);
        countSql.append("SELECT COUNT(*) FROM ").append(USER_TABLE);
        querySql.append("SELECT id,name,name_first_letter,nickname,avatar_url FROM ").append(USER_TABLE);
        whereSql.append(" id<>'00000000000000000000000000000000'");
        if(name != null && !name.trim().isEmpty()) {
            whereSql.append(" AND (name LIKE ? OR nickname LIKE ?)");
            ps.add(new Parameter("name", ParameterDataTypeEnum.STRING, "%" + name + "%"));
            ps.add(new Parameter("nickname", ParameterDataTypeEnum.STRING, "%" + name + "%"));
        }
        if(whereSql.length() > 0) {
            countSql.append(" WHERE ").append(whereSql.toString());
            querySql.append(" WHERE ").append(whereSql.toString());
        }
        if(limit > 0) {
            querySql.append(" LIMIT ").append(limit);
        }
        if(offset >= 0) {
            querySql.append(" OFFSET ").append(offset);
        }
        return new SqlBuildResult(countSql.toString(), querySql.toString(), ps);
    }
    
    /**
     * 构建更新语句
     * @param user
     * @param updateNullValueField
     * @return
     */
    private SqlBuildResult buildUpdateSql(User user, boolean updateNullValueField) {
        StringBuilder sql = new StringBuilder(256);
        List<Parameter> ps = new ArrayList<>(16);
        sql.append("UPDATE ").append(USER_TABLE).append(" SET ");
        boolean hasUpdateField = false;
        if((user.getName() != null && !user.getName().trim().isEmpty()) || updateNullValueField) {
            sql.append("name=?");
            ps.add(new Parameter("name", ParameterDataTypeEnum.STRING, user.getName()));
            hasUpdateField = true;
        }
        if((user.getNickname() != null && !user.getNickname().trim().isEmpty()) || updateNullValueField) {
            sql.append(hasUpdateField ? "," : "").append("nickname=?");
            ps.add(new Parameter("nickname", ParameterDataTypeEnum.STRING, user.getNickname()));
            hasUpdateField = true;
        }
        if(ps.size() > 0) {
            String firstLetterOfName = FirstLetterUtil.getFirstLetter(user.getNickname() != null && !user.getNickname().trim().isEmpty() ?
                    user.getNickname().trim() : user.getName().trim());
            sql.append(hasUpdateField ? "," : "").append("name_first_letter=?");
            ps.add(new Parameter("name_first_letter", ParameterDataTypeEnum.STRING, firstLetterOfName));
            hasUpdateField = true;
        }
        if((user.getSalt() != null && !user.getSalt().trim().isEmpty()) || updateNullValueField) {
            sql.append(hasUpdateField ? "," : "").append("salt=?");
            ps.add(new Parameter("salt", ParameterDataTypeEnum.STRING, user.getSalt()));
            hasUpdateField = true;
        }
        if((user.getPassword() != null && !user.getPassword().trim().isEmpty()) || updateNullValueField) {
            sql.append(hasUpdateField ? "," : "").append("password=?");
            ps.add(new Parameter("password", ParameterDataTypeEnum.STRING, user.getPassword()));
            hasUpdateField = true;
        }
        if(user.getLocked() != null || updateNullValueField) {
            sql.append(hasUpdateField ? "," : "").append("locked=?");
            hasUpdateField = true;
        }
        if((user.getAvatarUrl() != null && !user.getAvatarUrl().trim().isEmpty()) || updateNullValueField) {
            sql.append(hasUpdateField ? "," : "").append("avatar_url=?");
            ps.add(new Parameter("avatar_url", ParameterDataTypeEnum.STRING, user.getAvatarUrl()));
            hasUpdateField = true;
        }
        if(user.getLastPostAt() > 0) {
            sql.append(hasUpdateField ? "," : "").append("last_post_at=?");
            ps.add(new Parameter("last_post_at", ParameterDataTypeEnum.LONG, user.getLastPostAt()));
            hasUpdateField = true;
        }
        if((user.getOnlineStatus() != null && !user.getOnlineStatus().trim().isEmpty()) || updateNullValueField) {
            sql.append(hasUpdateField ? "," : "").append("online_status=?");
            ps.add(new Parameter("online_status", ParameterDataTypeEnum.STRING, user.getOnlineStatus()));
        }
        sql.append(" WHERE id=?");
        ps.add(new Parameter("id", ParameterDataTypeEnum.STRING, user.getId()));
        return new SqlBuildResult(sql.toString(), ps);
    }

}
