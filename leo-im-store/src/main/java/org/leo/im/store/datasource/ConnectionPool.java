package org.leo.im.store.datasource;

import java.sql.Connection;

/**
 * 数据库连接池接口
 * 
 * @author Leo
 * @date 2018/3/19
 */
public interface ConnectionPool {
	
	/**
	 * 得到数据库连接
	 * @return
	 */
	Connection getConnection();

}
