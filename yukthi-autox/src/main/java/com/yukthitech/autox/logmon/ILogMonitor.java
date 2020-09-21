package com.yukthitech.autox.logmon;

import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.test.TestCaseResult;

/**
 * Interface representing log monitors. Used to fetch logs from different sources for test cases.
 * 
 * Before starting test case, {@link #startMonitoring()} method would be invoked which is expected to mark start location on target log.
 * 
 * After test case execution is completed {@link #stopMonitoring()} method would be invoked which should fetch the log from start location till current point.
 * 
 * @author akiran
 */
public interface ILogMonitor
{
	/**
	 * Fetches the name of the log monitor.
	 * @return name
	 */
	public String getName();
	
	/**
	 * Checks if is if this flag is set, then only on error, the log will be fetched and added to report. Defaults to true.
	 *
	 * @return the if this flag is set, then only on error, the log will be fetched and added to report
	 */
	public boolean isOnErrorOnly();
	
	/**
	 * Checks if is flag indicating whether this log monitor is enabled or not.
	 *
	 * @return the flag indicating whether this log monitor is enabled or not
	 */
	public boolean isEnabled();

	/**
	 * Before starting test case, {@link #startMonitoring()} method would be invoked which is expected to mark start location on target log.
	 */
	public void startMonitoring(AutomationContext context);
	
	/**
	 * After test case execution is completed {@link #stopMonitoring()} method would be invoked which should fetch the log from start location 
	 * till current point.
	 * @param context current context
	 * @param testCaseResult Current test case result
	 * @return Log files content from start location till current point.
	 */
	public List<LogFile> stopMonitoring(AutomationContext context, TestCaseResult testCaseResult);
}
