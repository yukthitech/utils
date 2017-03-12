package com.yukthitech.automation.config;

import java.util.Properties;

import com.yukthitech.ccg.xml.util.ValueProvider;

/**
 * Value provider for #{} expressions in config and test case files. This class
 * can provider values from properties file, system and environment properties also.
 * @author akiran
 */
public class AppConfigValueProvider implements ValueProvider
{
	/**
	 * Application properties, main source of values.
	 */
	private Properties appProperties;

	/**
	 * Instantiates a new app config value provider.
	 *
	 * @param appProperties the app properties
	 */
	public AppConfigValueProvider(Properties appProperties)
	{
		if(appProperties == null)
		{
			appProperties = new Properties();
		}
		
		this.appProperties = appProperties;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.ValueProvider#getValue(java.lang.String)
	 */
	@Override
	public Object getValue(String name)
	{
		if(name.startsWith("system."))
		{
			name = name.substring("system.".length());
			return System.getProperty(name);
		}
		
		if(name.startsWith("env."))
		{
			name = name.substring("env.".length());
			return System.getenv(name);
		}
		
		return "" + appProperties.getProperty(name);
	}
}
