package com.yukthitech.autox;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.test.FullExecutionDetails;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.ReportGenerator;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseResult;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Function executor to execute specified function.
 * @author akiran
 */
public class FunctionExecutor
{
	/**
	 * Report generator for generating reports.
	 */
	private ReportGenerator reportGenerator = new ReportGenerator();
	
	private AutomationContext context;
	
	private File reportFolder;
	
	public FunctionExecutor(AutomationContext context)
	{
		this.context = context;
		this.reportFolder = context.getReportFolder();
	}

	/**
	 * Executes specified function.
	 * @param functionName Function to execute
	 * @param executionName Execution name used to create sub folder under main report folder.
	 */
	public Object execute(String functionName, String executionName)
	{
		Function function = context.getFunction(functionName);
		
		if(function == null)
		{
			throw new InvalidArgumentException("No function found with name: " + functionName);
		}
		
		File funcReportFolder = new File(this.reportFolder, executionName);
		
		if(!funcReportFolder.exists())
		{
			try
			{
				FileUtils.forceMkdir(funcReportFolder);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Failed to create report folder: {}", funcReportFolder.getPath());
			}
		}
		
		context.setReportFolder(funcReportFolder);
		
		//create object required for execution
		ExecutionLogger exeLogger = new ExecutionLogger(context, function.getName(), function.getName());
		TestCase dummy = new TestCase();
		dummy.setName(function.getName());
		
		TestSuite testSuite = new TestSuite(function.getName() + "-test-suite");
		testSuite.addTestCase(dummy);
		
		context.startLogMonitoring();
		
		boolean executedSucessfully = true;
		Date startTime = new Date();
		//Generate report
		TestCaseResult testCaseResult = null;
		
		Object functionResult = null;
	
		try
		{
			functionResult = function.execute(context, exeLogger, false);
		}catch(Exception ex)
		{
			executedSucessfully = false;
			exeLogger.error(ex, "An error occurred while executing function: {}", function.getName());
			testCaseResult = new TestCaseResult(dummy, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
					"An unhandled error occurred while executing test case.",
					startTime, new Date());

		}
		
		//when execution was successful
		if(testCaseResult == null)
		{
			testCaseResult = new TestCaseResult(dummy, TestStatus.SUCCESSFUL, exeLogger.getExecutionLogData(), null,
					startTime, new Date());
		}
		
		FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
		fullExecutionDetails.setReportName(context.getAppConfiguration().getReportName());
		fullExecutionDetails.addTestResult(testSuite, testCaseResult);
		
		if(executedSucessfully)
		{
			fullExecutionDetails.testSuiteCompleted(testSuite);
		}
		else
		{
			fullExecutionDetails.testSuiteFailed(testSuite, "Failed");
		}
		
		//stop monitoring logs
		Map<String, File> monitoringLogs = context.stopLogMonitoring(testCaseResult);

		reportGenerator.createLogFiles(context, testCaseResult, "action-plan", monitoringLogs, "Action output");
		reportGenerator.generateReports(funcReportFolder, fullExecutionDetails, context);
		
		context.setReportFolder(reportFolder);
		return functionResult;
	}
}
