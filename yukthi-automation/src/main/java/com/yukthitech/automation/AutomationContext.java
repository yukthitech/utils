package com.yukthitech.automation;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yukthitech.automation.config.ApplicationConfiguration;
import com.yukthitech.automation.config.IConfiguration;
import com.yukthitech.automation.logmon.ILogMonitor;

/**
 * Automation Context information. 
 * @author akiran
 */
public class AutomationContext
{
	/**
	 * Basic arguments specified from command line context.
	 */
	private BasicArguments basicArguments;
	
	/**
	 * Map to hold context attributes.
	 */
	private Map<String, Object> nameToAttr = new HashMap<String, Object>();
	
	/**
	 * Application configuration.
	 */
	private ApplicationConfiguration appConfiguration;
	
	/**
	 * Maintains list of required sub configurations required by loaded test suites.
	 */
	private Map<Class<?>, IConfiguration<?>> requiredConfiurations = new HashMap<>();
	
	/**
	 * List of configured log monitors.
	 */
	private Map<String, ILogMonitor> logMonitors;
	
	/**
	 * Constructor.
	 * @param appConfiguration Application configuration
	 */
	public AutomationContext(ApplicationConfiguration appConfiguration)
	{
		this.appConfiguration = appConfiguration;
		
		List<ILogMonitor> logMontLst = appConfiguration.getLogMonitors();
		
		if(logMontLst != null && !logMontLst.isEmpty())
		{
			logMonitors = logMontLst.stream().collect(Collectors.toMap(monit -> monit.getName() , monit -> monit));
		}
	}
	
	/**
	 * Sets the basic arguments specified from command line context.
	 *
	 * @param basicArguments the new basic arguments specified from command line context
	 */
	public void setBasicArguments(BasicArguments basicArguments)
	{
		this.basicArguments = basicArguments;
	}
	
	/**
	 * Gets the basic arguments specified from command line context.
	 *
	 * @return the basic arguments specified from command line context
	 */
	public BasicArguments getBasicArguments()
	{
		return basicArguments;
	}
	
	/**
	 * Sets the specified attribute with specified value.
	 * @param name Name of the attribute
	 * @param value Value of the attribute
	 */
	public void setAttribute(String name, Object value)
	{
		nameToAttr.put(name, value);
	}
	
	/**
	 * Fetches the attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getAttribute(String name)
	{
		return nameToAttr.get(name);
	}
	
	/**
	 * Removes attribute with specified name.
	 * @param name Name of the attribute to remove.
	 * @return Current attribute value.
	 */
	public Object removeAttribute(String name)
	{
		return nameToAttr.remove(name);
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttributeMap()
	{
		return Collections.unmodifiableMap(nameToAttr);
	}
	
	/**
	 * Adds required configuration to the context.
	 * @param configuration configuration to add
	 */
	void addRequireConfiguration(IConfiguration<?> configuration)
	{
		this.requiredConfiurations.put(configuration.getClass(), configuration);
	}
	
	/**
	 * Fetches all required configurations required by this context.
	 * @return required configurations.
	 */
	public Collection<IConfiguration<?>> getConfigurations()
	{
		return Collections.unmodifiableCollection(requiredConfiurations.values());
	}
	
	/**
	 * Fetches the configuration of specified configuration type.
	 * @param configType configuration type to fetch
	 * @return matching configuration
	 */
	@SuppressWarnings("unchecked")
	public <T extends IConfiguration<?>> T getConfiguration(Class<T> configType)
	{
		return (T) requiredConfiurations.get(configType);
	}
	
	/**
	 * Gets the application configuration.
	 *
	 * @return the application configuration
	 */
	public ApplicationConfiguration getAppConfiguration()
	{
		return appConfiguration;
	}
	
	/**
	 * Starts all registered log monitors.
	 */
	public synchronized void startLogMonitoring()
	{
		if(logMonitors == null)
		{
			return;
		}
		
		for(ILogMonitor monitor : logMonitors.values())
		{
			monitor.startMonitoring();
		}
	}
	
	/**
	 * Stops the log monitors and collects the log files generated.
	 * @return Collected log files
	 */
	public synchronized Map<String, File> stopLogMonitoring()
	{
		if(logMonitors == null)
		{
			return null;
		}
		
		Map<String, File> logFiles = new HashMap<>();
		
		for(Map.Entry<String, ILogMonitor> entry : this.logMonitors.entrySet())
		{
			logFiles.put(entry.getKey(), entry.getValue().stopMonitoring());
		}
		
		return logFiles;
	}
}
