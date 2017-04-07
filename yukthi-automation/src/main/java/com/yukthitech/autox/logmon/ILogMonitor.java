package com.yukthitech.autox.logmon;

import java.io.File;

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
	public void startMonitoring();
	
	/**
	 * After test case execution is completed {@link #stopMonitoring()} method would be invoked which should fetch the log from start location 
	 * till current point.
	 * @return Log content from start location till current point.
	 */
	public File stopMonitoring();
}
