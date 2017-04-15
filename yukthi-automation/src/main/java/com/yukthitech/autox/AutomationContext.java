package com.yukthitech.autox;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.logmon.ILogMonitor;
import com.yukthitech.autox.test.StepGroup;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Automation Context information. 
 * @author akiran
 */
public class AutomationContext
{
	/**
	 * Automation context that can be accessed anywhere needed.
	 */
	private static AutomationContext instance;
	
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
	 * Maintains list of required plugins required by loaded test suites.
	 */
	private Map<Class<?>, IPlugin<?>> requiredPlugins = new HashMap<>();
	
	/**
	 * List of configured log monitors.
	 */
	private Map<String, ILogMonitor> logMonitors;
	
	/**
	 * Work directory to be used for this context.
	 */
	private File workDirectory;
	
	/**
	 * Name to step group mapping.
	 */
	private Map<String, StepGroup> nameToGroup = new HashMap<>();

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
		
		this.workDirectory = new File(appConfiguration.getWorkDirectory());
		
		try
		{
			FileUtils.deleteDirectory(workDirectory);
			FileUtils.forceMkdir(workDirectory);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while cleaning up work directory", ex);
		}
		
		AutomationContext.instance = this;
	}
	
	/**
	 * Gets the automation context that can be accessed anywhere needed.
	 *
	 * @return the automation context that can be accessed anywhere needed
	 */
	public static AutomationContext getInstance()
	{
		return instance;
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
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttr()
	{
		return Collections.unmodifiableMap(nameToAttr);
	}
	
	/**
	 * Adds required plugin to the context.
	 * @param plugin plugin to add
	 */
	void addRequirePlugin(IPlugin<?> plugin)
	{
		this.requiredPlugins.put(plugin.getClass(), plugin);
	}
	
	/**
	 * Fetches all required plugins required by this context.
	 * @return required plugins.
	 */
	public Collection<IPlugin<?>> getPlugins()
	{
		return Collections.unmodifiableCollection(requiredPlugins.values());
	}
	
	/**
	 * Fetches the plugin of specified plugin type.
	 * @param pluginType plugin type to fetch
	 * @return matching plugin
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPlugin<?>> T getPlugin(Class<T> pluginType)
	{
		return (T) requiredPlugins.get(pluginType);
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
	
	/**
	 * Fetches the data bean map.
	 * @return data bean map.
	 */
	public Map<String, Object> getDataBeans()
	{
		return appConfiguration.getDataBeans();
	}
	
	/**
	 * Gets the work directory to be used for this context.
	 *
	 * @return the work directory to be used for this context
	 */
	public File getWorkDirectory()
	{
		return workDirectory;
	}
	
	/**
	 * Adds specified test group.
	 * @param stepGroup group to add.
	 */
	public void addStepGroup(StepGroup group)
	{
		if(nameToAttr.containsKey(group.getName()))
		{
			throw new InvalidStateException("Duplicate step group name encountered: {}", group.getName());
		}
		
		nameToGroup.put(group.getName(), group);
	}
	
	/**
	 * Adds the specified step groups.
	 *
	 * @param stepGroups step groups to add
	 */
	public void addStepGroups(Map<String, StepGroup> stepGroups)
	{
		nameToGroup.putAll(stepGroups);
	}
	
	/**
	 * Fetches the step group with specified name.
	 * @param name name of step group.
	 * @return matching group
	 */
	public StepGroup getStepGroup(String name)
	{
		return nameToGroup.get(name);
	}
}
