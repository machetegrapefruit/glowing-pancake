package database;

import configuration.Configuration;

public class DBConstants {
	public static final String dbURL = Configuration.getDefaultConfiguration().getDbUrl();
	public static final String dbName = Configuration.getDefaultConfiguration().getDbName();
}
