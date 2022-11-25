package com.yukthitech.autox.test.sql;

import javax.sql.DataSource;

import com.yukthitech.autox.config.AbstractPluginSession;

/**
 * DB Plugin session.
 * 
 * @author akranthikiran
 */
public class DbPluginSession extends AbstractPluginSession<DbPluginSession, DbPlugin>
{
	public DbPluginSession(DbPlugin parentPlugin)
	{
		super(parentPlugin);
	}
	
	/**
	 * Fetches data source with specified name.
	 * @param name Name of the data source.
	 * @return Matching data source name.
	 */
	public DataSource getDataSource(String name)
	{
		return parentPlugin.getDataSource(name);
	}
}
