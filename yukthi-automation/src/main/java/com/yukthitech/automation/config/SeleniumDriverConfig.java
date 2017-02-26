package com.yukthitech.automation.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Selenium driver configuration.
 * @author akiran
 */
public class SeleniumDriverConfig
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
}
