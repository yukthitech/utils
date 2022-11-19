package com.yukthitech.autox.logmon;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.context.AutomationContext;

public class LogMonitorManager
{
	private static LogMonitorManager instance;
	
	/**
	 * List of configured log monitors.
	 */
	private Map<String, ILogMonitor> logMonitors;

	private LogMonitorManager()
	{}
	
	public synchronized static LogMonitorManager getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		
		instance = new LogMonitorManager();
		List<ILogMonitor> monitors = AutomationContext.getInstance().getAppConfiguration().getLogMonitors();
		
		instance.logMonitors = new HashMap<>();
		
		if(CollectionUtils.isEmpty(monitors))
		{
			return instance;
		}
		
		monitors.forEach(monitor -> instance.logMonitors.put(monitor.getName(), monitor));
		return instance;
	}
	
	public Collection<ILogMonitor> getLogMonitors()
	{
		return logMonitors.values();
	}
}
