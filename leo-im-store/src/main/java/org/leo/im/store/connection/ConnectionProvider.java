package org.leo.im.store.connection;

import java.sql.Connection;

/**
 * 数据库连接提供者类
 * 
 * @author Leo
 * @date 2018/3/19
 */
public final class ConnectionProvider {
	
	/**
	 * 得到数据库连接
	 * @return
	 */
	public static Connection getConnection() {
		return ConnectionFactory.threadDbConnection.get();
	}

}
