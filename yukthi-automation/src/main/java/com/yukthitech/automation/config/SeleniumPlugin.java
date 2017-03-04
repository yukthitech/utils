package com.yukthitech.automation.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Plugin needed by selenium based steps or validators.
 * @author akiran
 */
public class SeleniumPlugin implements IPlugin<SeleniumPluginArgs>, Validateable
{
	private static Logger logger = LogManager.getLogger(SeleniumPlugin.class);
	
	/**
	 * Selenium drivers to use for automation.
	 */
	private Map<String, SeleniumDriverConfig> drivers = new HashMap<>();
	
	/**
	 * Active driver to be used for the current automation execution.
	 */
	private WebDriver activeDriver;
	
	/**
	 * Active driver name, useful during driver reset.
	 */
	private String activeDriverName;
	
	/**
	 * Base url of the application.
	 */
	private String baseUrl;
	
	@Override
	public Class<SeleniumPluginArgs> getArgumentBeanType()
	{
		return SeleniumPluginArgs.class;
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
	
	/**
	 * Sets the base url of the application.
	 *
	 * @param baseUrl the new base url of the application
	 */
	public void setBaseUrl(String baseUrl)
	{
		if(baseUrl.endsWith("/"))
		{
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Gets the base url of the application.
	 *
	 * @return the base url of the application
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}
	
	public String getResourceUrl(String resource)
	{
		if(!resource.startsWith("/"))
		{
			resource = "/" + resource;
		}
		
		return baseUrl + resource;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.config.IConfiguration#initialize(com.yukthitech.automation.AutomationContext, java.lang.Object)
	 */
	@Override
	public void initialize(AutomationContext context, SeleniumPluginArgs args)
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
			activeDriverName = driverName;
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
	
	/**
	 * Recreates the driver object. Note this method will not close
	 * the existing driver.
	 */
	public void resetDriver()
	{
		SeleniumDriverConfig driverConfig = drivers.get(activeDriverName);
		
		try
		{
			activeDriver = (WebDriver) Class.forName(driverConfig.getClassName()).newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating web driver {} of type - {}", driverConfig.getName(), driverConfig.getClassName());
		}
	}

	@Override
	public void validate() throws ValidateException
	{
		if(drivers.isEmpty())
		{
			throw new ValidateException("No drivers are specified.");
		}
		
		if(StringUtils.isBlank(baseUrl))
		{
			throw new ValidateException("No base url is specified.");
		}
	}
}
