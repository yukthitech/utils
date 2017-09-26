package com.yukthitech.autox.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Selenium driver configuration.
 * @author akiran
 */
public class SeleniumDriverConfig implements Validateable
{
	/**
	 * Name of the driver.
	 */
	private String name;
	
	/**
	 * Class name of the driver.
	 */
	private String className;
	
	/**
	 * System properties to be added to use this driver.
	 */
	private Map<String, String> systemProperties = new HashMap<>();
	
	/**
	 * Flag indicating if this driver is the default driver.
	 */
	private boolean isDefault;
	
	/**
	 * Default page to be opened to ensure driver is in active use. This is mainly required
	 * by headless browser (jbrowser).
	 */
	private String defaultPage;

	/**
	 * Gets the name of the driver.
	 *
	 * @return the name of the driver
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the driver.
	 *
	 * @param name the new name of the driver
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the class name of the driver.
	 *
	 * @return the class name of the driver
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * Sets the class name of the driver.
	 *
	 * @param className the new class name of the driver
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * Gets the system properties to be added to use this driver.
	 *
	 * @return the system properties to be added to use this driver
	 */
	public Map<String, String> getSystemProperties()
	{
		return systemProperties;
	}

	/**
	 * Sets the system properties to be added to use this driver.
	 *
	 * @param systemProperties the new system properties to be added to use this driver
	 */
	public void setSystemProperties(Map<String, String> systemProperties)
	{
		if(systemProperties == null)
		{
			throw new NullPointerException("System properties can not be null");
		}
		
		this.systemProperties = systemProperties;
	}
	
	/**
	 * Adds specified system property for this driver.
	 * @param name Name of the property.
	 * @param value value of the property.
	 */
	public void setSystemProperty(String name, String value)
	{
		this.systemProperties.put(name, value);
	}

	/**
	 * Checks if is default.
	 *
	 * @return true, if is default
	 */
	public boolean isDefault()
	{
		return isDefault;
	}

	/**
	 * Sets the default.
	 *
	 * @param isDefault the new default
	 */
	public void setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
	}

	/**
	 * Gets the default page to be opened to ensure driver is in active use. This is mainly required by headless browser (jbrowser).
	 *
	 * @return the default page to be opened to ensure driver is in active use
	 */
	public String getDefaultPage()
	{
		return defaultPage;
	}

	/**
	 * Sets the default page to be opened to ensure driver is in active use. This is mainly required by headless browser (jbrowser).
	 *
	 * @param defaultPage the new default page to be opened to ensure driver is in active use
	 */
	public void setDefaultPage(String defaultPage)
	{
		this.defaultPage = defaultPage;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(name))
		{
			throw new ValidateException("Name can not be null or empty.");
		}
		
		if(StringUtils.isBlank(className))
		{
			throw new ValidateException("Class-name can not be null or empty.");
		}
	}
}
