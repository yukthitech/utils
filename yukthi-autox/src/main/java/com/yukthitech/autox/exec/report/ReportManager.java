package com.yukthitech.autox.exec.report;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.exec.ExecutionType;
import com.yukthitech.autox.exec.Executor;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages the report generation.
 * @author akranthikiran
 */
public class ReportManager
{
	private static ReportManager instance = new ReportManager();
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private class ExecutorDetails
	{
		private Map<ExecutionType, ExecutionLogger> loggers;
		
		private ExecutionStatusReport statusReport = new ExecutionStatusReport();
		
		public ExecutionLogger getLoggerIfPresent(ExecutionType executionType)
		{
			if(loggers == null)
			{
				return null;
			}
			
			return loggers.get(executionType);
		}
		
		public ExecutionLogger getLogger(Executor executor, ExecutionType executionType, String suffix)
		{
			ExecutionLogger logger = null;
			
			if(loggers == null)
			{
				loggers = new HashMap<>();
			}
			else
			{
				logger = loggers.get(executionType);
			}
			
			if(logger == null)
			{
				String fileName = reportInfoProviders.getCode(executor) + "_" + reportInfoProviders.getName(executor) + suffix;
				logger = new ExecutionLogger(fileName, reportInfoProviders.getName(executor), reportInfoProviders.getDescription(executor));
				
				loggers.put(executionType, logger);
			}
			
			return logger;
		}
	}
	
	private ReportInfoProviders reportInfoProviders = new ReportInfoProviders();
	
	private IdentityHashMap<Executor, ExecutorDetails> executorDetailsMap = new IdentityHashMap<>();
	
	private ExecutorDetails rootExecutorDetails;
	
	public static ReportManager getInstance()
	{
		return instance;
	}
	
	private ExecutorDetails getExecutorDetails(Executor executor)
	{
		ExecutorDetails details = executorDetailsMap.get(executor);
		
		if(details == null)
		{
			details = new ExecutorDetails();
			executorDetailsMap.put(executor, details);
			
			if(rootExecutorDetails == null)
			{
				rootExecutorDetails = details;
			}
			
			if(executor.getParentExecutor() != null)
			{
				ExecutorDetails parentDetails = executorDetailsMap.get(executor.getParentExecutor());
				parentDetails.statusReport.addChidReport(details.statusReport);
			}
		}
		
		return details;
	}
	
	public synchronized ExecutionLogger getSetupExecutionLogger(Executor executor)
	{
		return getExecutorDetails(executor).getLogger(executor, ExecutionType.SETUP, "-setup.log");
	}

	public synchronized ExecutionLogger getCleanupExecutionLogger(Executor executor)
	{
		return getExecutorDetails(executor).getLogger(executor, ExecutionType.CLEANUP, "-cleanup.log");
	}

	public synchronized ExecutionLogger getExecutionLogger(Executor executor)
	{
		return getExecutorDetails(executor).getLogger(executor, ExecutionType.MAIN, ".log");
	}
	
	private void generateReport()
	{
		File reportFolder = AutomationContext.getInstance().getReportFolder();
		File reportFile = new File(reportFolder, "test-results.json");
		
		try
		{
			objectMapper.writeValue(reportFile, rootExecutorDetails);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to generate report file: " + reportFile.getPath(), ex);
		}
	}
	
	public synchronized void executionStarted(ExecutionType executionType, Executor executor)
	{
		ExecutorDetails executorDetails = getExecutorDetails(executor);
		
		switch (executionType)
		{
			case SETUP:
			{
				executorDetails.statusReport.setSetupExecutionDetails(new ExecutionDetails());
				break;
			}
			case CLEANUP:
			{
				executorDetails.statusReport.setCleanupExecutionDetails(new ExecutionDetails());
				break;
			}
			case MAIN:
			{
				executorDetails.statusReport.setMainExecutionDetails(new ExecutionDetails());
				break;
			}
			default:
			{
				throw new InvalidStateException("Unsupported execution type encountered: {}", executionType);
			}
		}
		
		generateReport();
	}
	
	private void setEndDetails(ExecutionType executionType, Executor executor, TestStatus status, String mssg)
	{
		ExecutorDetails executorDetails = getExecutorDetails(executor);
		Date endTime = null;
		ExecutionLogger logger = null;
		
		switch (executionType)
		{
			case SETUP:
			{
				endTime = executorDetails.statusReport.getSetupExecutionDetails().setEndDetails(status, mssg);
				logger = executorDetails.getLoggerIfPresent(ExecutionType.SETUP);
				break;
			}
			case CLEANUP:
			{
				endTime = executorDetails.statusReport.getCleanupExecutionDetails().setEndDetails(status, mssg);
				logger = executorDetails.getLoggerIfPresent(ExecutionType.CLEANUP);
				break;
			}
			case MAIN:
			{
				endTime = executorDetails.statusReport.getMainExecutionDetails().setEndDetails(status, mssg);
				logger = executorDetails.getLoggerIfPresent(ExecutionType.MAIN);
				break;
			}
			default:
			{
				throw new InvalidStateException("Unsupported execution type encountered: {}", executionType);
			}
		}
		
		if(logger != null)
		{
			logger.close(status, endTime);
		}
		
		generateReport();
	}
	
	public synchronized void executionCompleted(ExecutionType executionType, Executor executor)
	{
		setEndDetails(executionType, executor, TestStatus.SUCCESSFUL, null);
	}
	
	public synchronized void executionErrored(ExecutionType executionType, Executor executor, String message)
	{
		setEndDetails(executionType, executor, TestStatus.ERRORED, message);
	}
	
	public synchronized void executionFailed(ExecutionType executionType, Executor executor, String message)
	{
		setEndDetails(executionType, executor, TestStatus.FAILED, message);
	}

	public synchronized void executionSkipped(ExecutionType executionType, Executor executor, String reason)
	{
		setEndDetails(executionType, executor, TestStatus.SKIPPED, reason);
	}
}
