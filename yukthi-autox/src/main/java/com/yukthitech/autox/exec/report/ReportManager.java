package com.yukthitech.autox.exec.report;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.exec.IExecutor;

/**
 * Manages the report generation.
 * @author akranthikiran
 */
public class ReportManager
{
	private static ReportManager instance = new ReportManager();
	
	public static ReportManager getInstance()
	{
		return instance;
	}
	
	public ExecutionLogger getSetupExecutionLogger(AutomationContext context, IExecutor executor)
	{
		String fileName = executor.getCode() + "_" + executor.getName() + "-setup.log";
		return new ExecutionLogger(context, fileName, executor.getName(), executor.getDescription());
	}

	public ExecutionLogger getCleanupExecutionLogger(AutomationContext context, IExecutor executor)
	{
		String fileName = executor.getCode() + "_" + executor.getName() + "-cleanup.log";
		return new ExecutionLogger(context, fileName, executor.getName(), executor.getDescription());
	}

	public ExecutionLogger getExecutionLogger(AutomationContext context, IExecutor executor)
	{
		String fileName = executor.getCode() + "_" + executor.getName() + ".log";
		return new ExecutionLogger(context, fileName, executor.getName(), executor.getDescription());
	}
	
	public void setupErrored(AutomationContext context, IExecutor executor, ExecutionLogger logger)
	{
		
	}

	public void setupCompleted(AutomationContext context, IExecutor executor, ExecutionLogger logger)
	{
		
	}

	public void cleanupErrored(AutomationContext context, IExecutor executor, ExecutionLogger logger)
	{
		
	}

	public void cleanupCompleted(AutomationContext context, IExecutor executor, ExecutionLogger logger)
	{
		
	}
	
	public void executionSkipped(AutomationContext context, IExecutor executor, String reason)
	{
		
	}
}
