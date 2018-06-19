package org.leo.im.store.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库工具类
 * 
 * @author Leo
 * @date 2018/3/19
 */
public class DbUtils {

    private final static Logger logger = LoggerFactory.getLogger(DbUtils.class);

    /**
     * 关闭ResultSet
     * 
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 关闭Statement
     * 
     * @param stmt
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

}
