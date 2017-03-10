package com.yukthitech.automation.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.logmon.FileLogMonitor;
import com.yukthitech.automation.logmon.ILogMonitor;

/**
 * Application configuration for applications being automated.
 */
public class ApplicationConfiguration
{
	/**
	 * Static instance of current application configuration.
	 */
	private static ApplicationConfiguration applicationConfiguration;
	
	/**
	 * Name of the report. Length should be less than 30 chars.
	 */
	private String reportName = "Automation";
	
	/**
	 * Folder containing test suite xmls.
	 */
	private String testSuiteFolder;
	
	/**
	 * Base packages to be scanned for steps and validators.
	 */
	private Set<String> basePackages = new HashSet<>();
	
	/**
	 * Plugins configured which would be required by different steps and validators.
	 */
	private Map<Class<?>, IPlugin<?>> plugins = new HashMap<>();
	
	/**
	 * Test data beans that can be used by test cases.
	 */
	private Map<String, Object> dataBeans = new HashMap<>();
	
	/**
	 * List of log monitors to be used.
	 */
	private List<ILogMonitor> logMonitors = new ArrayList<>();
	
	/**
	 * Work directory in which temp files and folders will be created.
	 * The work directory by default will be deleted and recreated during starting.
	 */
	private String workDirectory = "./work";
	
	/**
	 * Date format to be used in reports.
	 */
	private String dateFomat = "dd/MM/YYYY";
	
	/**
	 * Date-time format to be used in reports.
	 */
	private String dateTimeFomat = "dd/MM/YYYY HH:mm:ss";
	
	/**
	 * Time format to be used in reports.
	 */
	private String timeFomat = "hh:mm:ss aa";
	
	public ApplicationConfiguration()
	{
		ApplicationConfiguration.applicationConfiguration = this;
	}
	
	/**
	 * Gets the static instance of current application configuration.
	 *
	 * @return the static instance of current application configuration
	 */
	public static ApplicationConfiguration getInstance()
	{
		return applicationConfiguration;
	}

	/**
	 * Sets the static instance of current application configuration.
	 *
	 * @param applicationConfiguration the new static instance of current application configuration
	 */
	public static void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration)
	{
		ApplicationConfiguration.applicationConfiguration = applicationConfiguration;
	}

	/**
	 * Gets the name of the report. Length should be less than 30 chars.
	 *
	 * @return the name of the report
	 */
	public String getReportName()
	{
		return reportName;
	}

	/**
	 * Sets the name of the report. Length should be less than 30 chars.
	 *
	 * @param reportName the new name of the report
	 */
	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}

	/**
	 * Gets the folder containing test suite xmls.
	 *
	 * @return the folder containing test suite xmls
	 */
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
	
	/**
	 * Base package names where validations and steps needs to be scanned.
	 * @return Base package names
	 */
	public Set<String> getBasePackages()
	{
		return basePackages;
	}

	/**
	 * Generic adder for adding any type of plugin object. 
	 * @param plugin plugin to be added.
	 */
	public void addPlugin(IPlugin<?> plugin)
	{
		this.plugins.put(plugin.getClass(), plugin);
	}
	
	/**
	 * Sets specified selenium plugin.
	 * @param plugin selenium plugin to set
	 */
	public void setSeleniumPlugin(SeleniumPlugin plugin)
	{
		this.addPlugin(plugin);
	}
	
	/**
	 * Sets specified db plugin.
	 * @param plugin db plugin to set
	 */
	public void setDbPlugin(DbPlugin plugin)
	{
		this.addPlugin(plugin);
	}
	
	/**
	 * Gets the plugin from configured plugins of specified type.
	 * @return Matching plugin.
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPlugin<?>> T getPlugin(Class<T> pluginType)
	{
		return (T) plugins.get(pluginType);
	}
	
	/**
	 * Fetches all plugins.
	 * @return all plugins
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<IPlugin<?>> getAllPlugins()
	{
		return (Collection) plugins.values();
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
	
	/**
	 * Gets the test data beans that can be used by test cases.
	 *
	 * @return the test data beans that can be used by test cases
	 */
	public Map<String, Object> getDataBeans()
	{
		return dataBeans;
	}
	
	/**
	 * Adds log monitor to the configuration.
	 * @param monitor monitor to add
	 */
	public void addLogMonitor(ILogMonitor monitor)
	{
		if(monitor == null)
		{
			throw new NullPointerException("Monitor can not be null");
		}
		
		this.logMonitors.add(monitor);
	}
	
	/**
	 * Adds file log monitor to this configuration.
	 * @param monitor monitor to add.
	 */
	public void addFileLogMonitor(FileLogMonitor monitor)
	{
		addLogMonitor(monitor);
	}
	
	/**
	 * Gets the list of log monitors to be used.
	 *
	 * @return the list of log monitors to be used
	 */
	public List<ILogMonitor> getLogMonitors()
	{
		return logMonitors;
	}

	/**
	 * Gets the work directory in which temp files and folders will be created. The work directory by default will be deleted and recreated during starting.
	 *
	 * @return the work directory in which temp files and folders will be created
	 */
	public String getWorkDirectory()
	{
		return workDirectory;
	}

	/**
	 * Sets the work directory in which temp files and folders will be created. The work directory by default will be deleted and recreated during starting.
	 *
	 * @param workDirectory the new work directory in which temp files and folders will be created
	 */
	public void setWorkDirectory(String workDirectory)
	{
		if(StringUtils.isBlank(workDirectory))
		{
			throw new NullPointerException("Work directory can not be null or empty.");
		}
		
		this.workDirectory = workDirectory;
	}

	/**
	 * Gets the date format to be used in reports.
	 *
	 * @return the date format to be used in reports
	 */
	public String getDateFomat()
	{
		return dateFomat;
	}

	/**
	 * Sets the date format to be used in reports.
	 *
	 * @param dateFomat the new date format to be used in reports
	 */
	public void setDateFomat(String dateFomat)
	{
		this.dateFomat = dateFomat;
	}

	/**
	 * Gets the date-time format to be used in reports.
	 *
	 * @return the date-time format to be used in reports
	 */
	public String getDateTimeFomat()
	{
		return dateTimeFomat;
	}

	/**
	 * Sets the date-time format to be used in reports.
	 *
	 * @param dateTimeFomat the new date-time format to be used in reports
	 */
	public void setDateTimeFomat(String dateTimeFomat)
	{
		this.dateTimeFomat = dateTimeFomat;
	}

	/**
	 * Gets the time format to be used in reports.
	 *
	 * @return the time format to be used in reports
	 */
	public String getTimeFomat()
	{
		return timeFomat;
	}

	/**
	 * Sets the time format to be used in reports.
	 *
	 * @param timeFomat the new time format to be used in reports
	 */
	public void setTimeFomat(String timeFomat)
	{
		this.timeFomat = timeFomat;
	}
}
