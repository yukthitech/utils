package com.yukthitech.automation.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.yukthitech.automation.AutomationContext;

/**
 * plugin related to db related steps or validators.
 * @author akiran
 */
public class DbPlugin implements IPlugin<Object>
{
	/**
	 * Application data sources.
	 */
	private Map<String, DataSource> dataSourceMap = new HashMap<>();

	@Override
	public Class<Object> getArgumentBeanType()
	{
		return null;
	}
	
	/**
	 * Adds specified data source with specified name.
	 * @param name Name of the data source.
	 * @param dataSource Data source to add.
	 */
	public void addDataSource(String name, DataSource dataSource)
	{
		this.dataSourceMap.put(name, dataSource);
	}
	
	/**
	 * Fetches data source with specified name.
	 * @param name Name of the data source.
	 * @return Matching data source name.
	 */
	public DataSource getDataSource(String name)
	{
		return dataSourceMap.get(name);
	}

	@Override
	public void initialize(AutomationContext context, Object args)
	{
	}
}
