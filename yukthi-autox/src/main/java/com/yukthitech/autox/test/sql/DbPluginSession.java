package com.yukthitech.autox.test.sql;

import javax.sql.DataSource;

import com.yukthitech.autox.config.IPluginSession;

/**
 * DB Plugin session.
 * 
 * @author akranthikiran
 */
public class DbPluginSession implements IPluginSession
{
	private DbPlugin parentPlugin;

	public DbPluginSession(DbPlugin parentPlugin)
	{
		this.parentPlugin = parentPlugin;
	}
	
	@Override
	public DbPlugin getParentPlugin()
	{
		return parentPlugin;
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
	
	/**
	 * Fetches default data source if one is configured.
	 * @return default data source, if any.
	 */
	public DataSource getDefaultDataSource()
	{
		return parentPlugin.getDefaultDataSource();
	}

}
