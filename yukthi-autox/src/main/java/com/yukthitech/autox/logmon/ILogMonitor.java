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
