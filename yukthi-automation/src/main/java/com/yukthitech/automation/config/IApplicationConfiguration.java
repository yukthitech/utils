package com.yukthitech.automation.config;

import java.util.Collection;
import java.util.Set;

/**
 * Application configuration providing all required configuration for test automation. 
 * @author akiran
 */
public interface IApplicationConfiguration
{
	/**
	 * Gets the test cases folder where test suite files are present.
	 * @return folder path containing test suite files.
	 */
	public String getTestSuiteFolder();

	/**
	 * Gets the configuration from configured configurations of specified type.
	 * @return Matching configuration.
	 */
	public <T extends IConfiguration<?>> T getConfiguration(Class<T> configurationType);
	
	/**
	 * Fetches all configurations.
	 * @return all configurations
	 */
	public Collection<IConfiguration<?>> getAllConfigurations();
	
	/**
	 * Base package names where validations and steps needs to be scanned.
	 * @return Base package names
	 */
	public Set<String> getBasePackages();
	
	/**
	 * Gets data bean with specified name.
	 * @param name Name of the data bean
	 * @return matching data bean
	 */
	public Object getDataBean(String name);
}
