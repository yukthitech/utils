package com.yukthitech.autox.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.test.log.LogLevel;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Plugin needed by selenium based steps or validators.
 * @author akiran
 */
@Executable(name = "SeleniumPlugin", message = "Plugin needed by selenium/ui-automation based steps or validators.")
public class SeleniumPlugin implements IPlugin<SeleniumPluginArgs>, Validateable
{
	private static Logger logger = LogManager.getLogger(SeleniumPlugin.class);
	
	/**
	 * Selenium drivers to use for automation.
	 */
	@Param(description = "Name to basic configuration to be used for different drivers. Like - name, class-name and default system properties to set.", required = true)
	private Map<String, SeleniumDriverConfig> drivers = new LinkedHashMap<>();
	
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
	@Param(description = "Base url to be used for ui automation", required = true)
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
	public void addDriver(SeleniumDriverConfig driverConfig) throws ValidateException
	{
		driverConfig.validate();
		
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
			
			if(driverConfig.getDefaultPage() != null)
			{
				logger.debug("Taking driver to default page: " + driverConfig.getDefaultPage());
				activeDriver.get(driverConfig.getDefaultPage());
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating web driver {} of type - {}", driverConfig.getName(), driverConfig.getClassName(), ex);
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
	public void handleError(AutomationContext context, ErrorDetails errorDetails)
	{
		File file = ((TakesScreenshot) activeDriver).getScreenshotAs(OutputType.FILE);
		errorDetails.getExecutionLogger().logImage(null, "error-screenshot", "Screen shot during error", file, LogLevel.ERROR);
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
