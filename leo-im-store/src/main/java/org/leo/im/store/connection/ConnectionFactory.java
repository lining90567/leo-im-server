package org.leo.im.store.connection;

import java.sql.Connection;

/**
 * 数据库连接工厂接口
 * 
 * @author Leo
 * @date 2018/3/19
 */
public interface ConnectionFactory {
	
	/**
	 * 数据库连接ThreadLocal
	 */
	ThreadLocal<Connection> threadDbConnection = new ThreadLocal<Connection>();
	
	/**
	 * 得到数据库连接
	 * @return
	 */
	Connection getConnection();
	
	/**
	 * 得到数据库连接
	 * @param connectionString 连接字符串，开发或测试环境下需要传入连接字符串，生产环境下无需传入。
	 * @return
	 */
	Connection getConnection(String connectionString);
	
	/**
	 * 关闭数据库连接
	 */
	void closeConnection();

}
