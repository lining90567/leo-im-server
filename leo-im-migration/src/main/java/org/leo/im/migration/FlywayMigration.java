package org.leo.im.migration;

import org.flywaydb.core.Flyway;

public class FlywayMigration {
	
	/**
	 * 数据库迁移
	 * @param url JDBC URL
	 * @param user 数据库用户
	 * @param password 数据库口令
	 */
	public void migrate(String url, String user, String password) {
		Flyway flyway = new Flyway();
		flyway.setDataSource(url, user, password);
		flyway.migrate();
	}

}
