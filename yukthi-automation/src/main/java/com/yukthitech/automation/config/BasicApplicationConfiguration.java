package com.yukthitech.automation.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Class AbstractApplicationConfiguration.
 */
public class BasicApplicationConfiguration implements IApplicationConfiguration
{
	/**
	 * Folder containing test suite xmls.
	 */
	private String testSuiteFolder;
	
	/**
	 * Base packages to be scanned for steps and validators.
	 */
	private Set<String> basePackages = new HashSet<>();
	
	/**
	 * Subconfigurations configured which would be required by different steps and validators.
	 */
	private Map<Class<?>, IConfiguration<?>> configurations = new HashMap<>();
	
	/**
	 * Test data beans that can be used by test cases.
	 */
	private Map<String, Object> dataBeans = new HashMap<>();

	/**
	 * Gets the folder containing test suite xmls.
	 *
	 * @return the folder containing test suite xmls
	 */
	@Override
	public String getTestSuiteFolder()
	{
		return testSuiteFolder;
	}

	/**
	 * Sets the folder containing test suite xmls.
	 *
	 * @param testSuiteFolder the new folder containing test suite xmls
	 */
	public void setTestSuiteFolder(String testSuiteFolder)
	{
		this.testSuiteFolder = testSuiteFolder;
	}

	/**
	 * Adds specified base package for scanning.
	 * @param basePackage Base package to be added
	 */
	public void addBasePackage(String basePackage)
	{
		this.basePackages.add(basePackage);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.automation.IApplicationConfiguration#getBasePackages()
	 */
	@Override
	public Set<String> getBasePackages()
	{
		return basePackages;
	}

	/**
	 * Generic adder for adding any type of configuration object. 
	 * @param configuration configuration to be added.
	 */
	public void addConfiguration(IConfiguration<?> configuration)
	{
		this.configurations.put(configuration.getClass(), configuration);
	}
	
	/**
	 * Sets specified selenium configuration.
	 * @param configuration selenium configuration to set
	 */
	public void setSeleniumConfiguration(SeleniumConfiguration configuration)
	{
		this.addConfiguration(configuration);
	}
	
	/**
	 * Sets specified db configuration.
	 * @param configuration db configuration to set
	 */
	public void setDbConfiguration(DbConfiguration configuration)
	{
		this.addConfiguration(configuration);
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.automation.config.IApplicationConfiguration#getConfiguration(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IConfiguration<?>> T getConfiguration(Class<T> configurationType)
	{
		return (T) configurations.get(configurationType);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<IConfiguration<?>> getAllConfigurations()
	{
		return (Collection) configurations.values();
	}

	/**
	 * Adds specified data bean to this test suite.
	 * @param name Name of the data bean.
	 * @param bean Bean to be added.
	 */
	public void addDataBean(String name, Object bean)
	{
		dataBeans.put(name, bean);
	}
	
	/**
	 * Gets data bean with specified name.
	 * @param name Name of the data bean
	 * @return matching data bean
	 */
	public Object getDataBean(String name)
	{
		return dataBeans.get(name);
	}
}
