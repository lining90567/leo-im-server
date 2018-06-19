package org.leo.im.store.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.leo.im.store.exception.DAOException;
import org.leo.im.store.support.Parameter;

/**
 * dao基类
 * 
 * @author Leo
 * @date 2018/3/30
 */
public interface BaseDAO {
    
    String CONNECTION_NOT_FOUND_EXCEPTION = "Connection Not Found";

    /**
     * 创建id
     * 
     * @return
     */
    default String createId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 设置PreparedStatement的参数
     * 
     * @param stmt
     * @param parameters
     * @throws SQLException
     * @throws NumberFormatException
     */
    default void setPreparedStatmentParameters(PreparedStatement stmt, List<Parameter> parameters)
            throws NumberFormatException, SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                switch (parameters.get(i).getDataType()) {
                case SHORT:
                    stmt.setShort(i + 1, Short.parseShort(parameters.get(i).getValue().toString()));
                    break;
                case INT:
                    stmt.setInt(i + 1, Integer.parseInt(parameters.get(i).getValue().toString()));
                    break;
                case LONG:
                    stmt.setLong(i + 1, Long.parseLong(parameters.get(i).getValue().toString()));
                    break;
                case FLOAT:
                    stmt.setFloat(i + 1, Float.parseFloat(parameters.get(i).getValue().toString()));
                    break;
                case DOUBLE:
                    stmt.setDouble(i + 1, Double.parseDouble(parameters.get(i).getValue().toString()));
                    break;
                case STRING:
                    stmt.setString(i + 1, parameters.get(i).getValue().toString());
                    break;
                case DATE:
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date formattedDate = dateFormatter.parse(parameters.get(i).getValue().toString());
                        stmt.setDate(i + 1, new java.sql.Date(formattedDate.getTime()));
                    } catch (ParseException e) {
                        throw new DAOException(e);
                    }
                    break;
                case DATETIME:
                    Date date = (Date) parameters.get(i).getValue();
                    stmt.setDate(i + 1, new java.sql.Date(date.getTime()));
                    break;
                case TIMESTAMP:
                    Date dt = (Date) parameters.get(i).getValue();
                    SimpleDateFormat timestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    stmt.setTimestamp(i + 1, java.sql.Timestamp.valueOf(timestampFormatter.format(dt)));
                    break;
                case BOOLEAN:
                    stmt.setBoolean(i + 1, Boolean.parseBoolean(parameters.get(i).getValue().toString()));
                    break;
                case OBJECT:
                    stmt.setObject(i + 1, parameters.get(i));
                    break;
                default:
                    stmt.setObject(i + 1, parameters.get(i));
                }
            }
        }
    }

}
