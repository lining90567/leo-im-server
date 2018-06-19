package org.leo.im.store.connection.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.leo.im.store.connection.ConnectionFactory;
import org.leo.im.store.datasource.ConnectionPool;
import org.leo.im.store.datasource.impl.DruidConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库连接池连接工厂类
 * 
 * @author Leo
 * @date 2018/3/19
 */
public final class PoolConnectionFactory implements ConnectionFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(PoolConnectionFactory.class);
	
	/**
	 * 私有构造函数，避免外部创建实例。
	 */
	private PoolConnectionFactory() {
		
	}
	
	private static class InstanceHolder {
		private static PoolConnectionFactory instance = new PoolConnectionFactory();
	}
	
	/**
	 * 得到单例实例
	 * @return
	 */
	public static PoolConnectionFactory getInstance() {
		return InstanceHolder.instance;
	}
	
	/**
	 * 得到数据库连接
	 * @return
	 */
	@Override
	public Connection getConnection() {
		return getConnection(null);
	}

	/**
	 * 得到数据库连接
	 * @param connectionString 连接字符串，开发或测试环境下需要传入连接字符串，生产环境下无需传入。
	 * @return
	 */
	@Override
	public Connection getConnection(String connectionString) {
		Connection conn = threadDbConnection.get();
		if(conn == null) {
			conn = getConnectionPool().getConnection();
			threadDbConnection.set(conn);
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 */
	@Override
	public void closeConnection() {
		Connection conn = threadDbConnection.get();
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			threadDbConnection.remove();
		}
	}
	
	/**
	 * 得到连接池实例
	 * @return
	 */
	private ConnectionPool getConnectionPool() {
		return DruidConnectionPool.getInstance();
	}

}
