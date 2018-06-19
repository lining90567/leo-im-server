package org.leo.im.store.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.leo.im.model.File;
import org.leo.im.store.connection.ConnectionProvider;
import org.leo.im.store.dao.BaseDAO;
import org.leo.im.store.dao.FileDAO;
import org.leo.im.store.exception.DAOException;
import org.leo.im.store.util.DbUtils;

/**
 * 文件dao jdbc实现类
 * 
 * @author Leo
 * @date 2018/6/13
 */
public class JdbcFileDAOImpl implements BaseDAO, FileDAO {
    
    private final static String FILE_TABLE = "im_file";

    /**
     * 添加文件
     * @param file
     * @return
     */
    @Override
    public String save(File file) {
        Connection conn = ConnectionProvider.getConnection();
        if (conn == null) {
            throw new DAOException(CONNECTION_NOT_FOUND_EXCEPTION);
        }
        StringBuilder sql = new StringBuilder(256);
        sql.append("INSERT INTO ").append(FILE_TABLE).append("(id,name,extension,size,mime_typ,width,height,path,thumb_width,thumb_height) ");
        sql.append("VALUES(?,?,?,?,?,?,?,?,?,?)");
        
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql.toString());
            String fileId = createId();
            stmt.setString(1, fileId);
            stmt.setString(2, file.getName());
            stmt.setString(3, file.getExtension());
            stmt.setInt(4, file.getSize());
            stmt.setString(5,file.getMimeType());
            stmt.setInt(6, file.getWidth());
            stmt.setInt(7, file.getHeight());
            stmt.setString(8, file.getPath());
            stmt.setShort(9, file.getThumbWidth());
            stmt.setShort(10, file.getThumbHeight());
            if(stmt.executeUpdate() > 0) {
                return fileId;
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            DbUtils.closeStatement(stmt);
        }
    }

}
