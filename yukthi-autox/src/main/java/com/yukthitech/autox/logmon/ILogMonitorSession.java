package com.yukthitech.autox.logmon;

import java.util.List;

import com.yukthitech.autox.context.ReportLogFile;

/**
 * Session of log monitor. Framework will ensure only one thread will access one session.
 * @author akranthikiran
 */
public interface ILogMonitorSession
{
	public ILogMonitor getParentMonitor();
	
	/**
	 * Before starting test case, {@link #startMonitoring()} method would be invoked which is expected to mark start location on target log.
	 */
	public void startMonitoring();
	
	/**
	 * After test case execution is completed {@link #stopMonitoring()} method would be invoked which should fetch the log from start location 
	 * till current point.
	 * @param context current context
	 * @return Log files content from start location till current point.
	 */
	public List<ReportLogFile> stopMonitoring();
}
