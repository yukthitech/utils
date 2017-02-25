package com.yukthitech.automation.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Configuration needed by selenium.
 * @author akiran
 */
public class SeleniumConfiguration implements IConfiguration<SeleniumConfigurationArgs>
{
	private static Logger logger = LogManager.getLogger(SeleniumConfiguration.class);
	
	/**
	 * Selenium drivers to use for automation.
	 */
	private Map<String, SeleniumDriverConfig> drivers = new HashMap<>();
	
	/**
	 * Active driver to be used for the current automation execution.
	 */
	private WebDriver activeDriver;
	
	@Override
	public Class<SeleniumConfigurationArgs> getArgumentBeanType()
	{
		return SeleniumConfigurationArgs.class;
	}

	/**
	 * Adds specified driver configuration.
	 * @param driverConfig configuration to be added
	 */
	public void addDriver(SeleniumDriverConfig driverConfig)
	{
		drivers.put(driverConfig.getName(), driverConfig);
	}
	
	/**
	 * Fetches the first default driver name. If no default driver
	 * is specified, first driver will be used as default one. 
	 * @return Default driver name
	 */
	private String getDefaultDriverName()
	{
		String firstName = null;
		
		for(String name : drivers.keySet())
		{
			firstName = firstName == null ? name : firstName;
			
			if(drivers.get(name).isDefault())
			{
				return name;
			}
		}
		
		return firstName;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.config.IConfiguration#initialize(com.yukthitech.automation.AutomationContext, java.lang.Object)
	 */
	@Override
	public void initialize(AutomationContext context, SeleniumConfigurationArgs args)
	{
		String driverName = args.getWebDriver();
		
		if(driverName == null)
		{
			driverName = getDefaultDriverName();
			
			logger.debug("As no web driver is specified in command line arguments, using default driver - " + driverName);
		}
		else
		{
			logger.debug("Using driver specified in command line argument - " + driverName);
		}
		
		SeleniumDriverConfig driverConfig = drivers.get(driverName);
		
		if(driverConfig == null)
		{
			throw new InvalidStateException("No web driver is defined with name - {}", driverName);
		}
		
		Map<String, String> sysProp = driverConfig.getSystemProperties();
		logger.debug("Loading system properties for driver '{}' as - {}", driverName, sysProp);
		
		for(String name : sysProp.keySet())
		{
			System.setProperty(name, sysProp.get(name));
		}
		
		try
		{
			activeDriver = (WebDriver) Class.forName(driverConfig.getClassName()).newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating web driver {} of type - {}", driverConfig.getName(), driverConfig.getClassName());
		}
	}
	
	/**
	 * Fetches the current web driver.
	 * @return current web driver.
	 */
	public WebDriver getWebDriver()
	{
		return activeDriver;
	}
}
