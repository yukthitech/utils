package com.yukthitech.autox.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Param;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * plugin related to db related steps or validators.
 * @author akiran
 */
@Executable(name = "DbPlugin", message = "Plugin related to db related steps or validators.")
public class DbPlugin implements IPlugin<Object>, Validateable
{
	/**
	 * Application data sources.
	 */
	@Param(description = "Name to data source mapping for different data sources that are available in this app automation.", required = true)
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
	public DataSource getDataSource(String name)
	{
		return dataSourceMap.get(name);
	}

	@Override
	public void initialize(AutomationContext context, Object args)
	{
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
