package org.leo.im.store.datasource.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.leo.im.store.datasource.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * druid数据库连接池实现类
 * 
 * @author Leo
 * @date 2018/3/19
 */
public class DruidConnectionPool implements ConnectionPool {
	
	private final static Logger logger = LoggerFactory.getLogger(DruidConnectionPool.class);
	
	// Druid数据源
	private static DruidDataSource dataSource = null;

	/**
	 * 私有构造函数，避免外部创建实例。
	 */
	private DruidConnectionPool() {

	}

	private static class InstanceHolder {
		static {
			Properties druidProps = new Properties();
			Properties sysProps = System.getProperties();			
			sysProps.forEach((key, value) -> {
				if(key.toString().startsWith("db.pool.")) {
					druidProps.put(key.toString().replace("db.pool.", ""), value);
				}
			});
			try {
				dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(druidProps);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		private static DruidConnectionPool instance = new DruidConnectionPool();
	}

	/**
	 * 得到单例实例
	 * 
	 * @return
	 */
	public static DruidConnectionPool getInstance() {
		return InstanceHolder.instance;
	}

	/**
	 * 得到数据库连接
	 * 
	 * @return
	 */
	@Override
	public Connection getConnection() {
		try {
			return dataSource == null ? null : dataSource.getConnection();
		} catch (SQLException e) {
			return null;
		}
	}

}
