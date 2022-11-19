package com.yukthitech.autox.test.sql;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * plugin related to db related steps or validators.
 * @author akiran
 */
@Executable(name = "DbPlugin", group = Group.NONE, message = "Plugin related to db related steps or validators.")
public class DbPlugin implements IPlugin<Object, DbPluginSession>, Validateable
{
	/**
	 * Application data sources.
	 */
	@Param(description = "Name to data source mapping for different data sources that are available in this app automation.", required = true)
	private Map<String, DataSource> dataSourceMap = new HashMap<>();
	
	/**
	 * Name of the default data source name.
	 */
	@Param(description = "Name of the default data source name.", required = false)
	private String defaultDataSource;

	/**
	 * Sets the name of the default data source name.
	 *
	 * @param defaultDataSource the new name of the default data source name
	 */
	public void setDefaultDataSource(String defaultDataSource)
	{
		this.defaultDataSource = defaultDataSource;
	}
	
	/**
	 * Adds specified data source with specified name.
	 * @param name Name of the data source.
	 * @param dataSource Data source to add.
	 */
	public void addDataSource(String name, DataSource dataSource)
	{
		if(StringUtils.isBlank(name))
		{
			throw new NullPointerException("Data source name can not be null or empty.");
		}
		
		if(dataSource == null)
		{
			throw new NullPointerException("Data source can not be null");
		}
		
		this.dataSourceMap.put(name, dataSource);
	}
	
	/**
	 * Fetches data source with specified name.
	 * @param name Name of the data source.
	 * @return Matching data source name.
	 */
	DataSource getDataSource(String name)
	{
		return dataSourceMap.get(name);
	}
	
	/**
	 * Fetches default data source if one is configured.
	 * @return default data source, if any.
	 */
	DataSource getDefaultDataSource()
	{
		return dataSourceMap.get(defaultDataSource);
	}

	@Override
	public DbPluginSession newSession()
	{
		return new DbPluginSession(this);
	}
	
	@Override
	public void close() throws Exception
	{
		for(DataSource dataSource : this.dataSourceMap.values())
		{
			if(dataSource instanceof BasicDataSource)
			{
				((BasicDataSource) dataSource).close();
			}
		}
	}

	@Override
	public void validate() throws ValidateException
	{
		if(dataSourceMap.isEmpty())
		{
			throw new ValidateException("No data sources are defined.");
		}
	}
}
