package com.yukthitech.autox.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.event.AutomationEvent;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Test suites executors.
 * @author akiran
 */
public class TestSuiteExecutor
{
	private static Logger logger = LogManager.getLogger(TestSuiteExecutor.class);
	
	private static AtomicInteger nextFileIndex = new AtomicInteger();
	
	/**
	 * Application context to be used.
	 */
	private AutomationContext context;
	
	/**
	 * Test suites to be executed.
	 */
	private TestSuiteGroup testSuiteGroup;
	
	/**
	 * Folder in which reports should be generated.
	 */
	private File reportFolder;
	
	/**
	 * Used for generating reports.
	 */
	private ReportGenerator reportGenerator = new ReportGenerator();
	
	/**
	 * Holds execution details.
	 */
	private FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
	
	/**
	 * At given time, this will hold value of test suite being executed.
	 */
	private TestSuite currentTestSuite;
	
	/**
	 * Instantiates a new test suite executor.
	 *
	 * @param context the context
	 * @param testSuiteGroup the test suite map
	 */
	public TestSuiteExecutor(AutomationContext context, TestSuiteGroup testSuiteGroup)
	{
		this.context = context;
		this.testSuiteGroup = testSuiteGroup;
		this.reportFolder = context.getReportFolder();

		fullExecutionDetails.setReportName(context.getAppConfiguration().getReportName());
	}
	
	/**
	 * Executes the test case. If data provider is specified, test case is executed for each data object.
	 *
	 * @param context
	 *            the context
	 * @return the test case result
	 */
	public TestCaseResult executeTestCase(AutomationContext context, List<TestCaseResult> testCaseDatsResults, TestCase testCase, String testFileName)
	{
		IDataProvider dataProvider = testCase.getDataProvider();
		
		if(dataProvider == null)
		{
			ExecutionLogger exeLogger = new ExecutionLogger(context, testCase.getName(), testCase.getDescription());
			TestCaseResult result = null;
			
			context.getAutomationListener().testCaseStarted(new AutomationEvent(currentTestSuite, testCase, null));
			
			//start monitoring logs
			context.startLogMonitoring();
			context.setActiveTestCase(testCase, null);
			
			testCase.setData(null);
			context.getExecutionStack().push(testCase);
			
			try
			{
				result = testCase.execute(context, exeLogger);
			}catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing test case: {}", testCase.getName());
				result = new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), "An unhandled error occurred while executing test case.");
			} finally
			{
				context.getExecutionStack().pop(testCase);
				context.clearActiveTestCase();
			}
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring();
			reportGenerator.createLogFiles(context, result, testFileName, monitoringLogs, testCase.getDescription());
			
			context.getAutomationListener().testCaseCompleted(new AutomationEvent(currentTestSuite, testCase, result));
			
			return result;
		}
		
		List<TestCaseData> dataLst = dataProvider.getStepData();

		//Log error and fail test case if data is missing
		if(dataLst == null)
		{
			return new TestCaseResult(testCase.getName(), TestStatus.ERRORED, null, "No data from data-provider. Data Provider: " + dataProvider.getName());
		}
		
		TestCaseResult result = null;
		ExecutionLogger exeLogger = null;
		String name = null;
		TestStatus finalStatus = TestStatus.SUCCESSFUL;
		
		for(TestCaseData data : dataLst)
		{
			name = testCase.getName() + " [" + data.getName() + "]";
			exeLogger = new ExecutionLogger(context, name, testCase.getDescription());
			
			exeLogger.debug(null, "Executing test case '{}' with data: {}", name, data.getName());
					
			//set data provider data on context
			context.setAttribute(dataProvider.getName(), data.getValue());

			//start monitoring logs
			context.startLogMonitoring();
			context.setActiveTestCase(testCase, data);
			testCase.setData(data);
			
			context.getExecutionStack().push(testCase);

			//execute test case for current data
			try
			{
				result = testCase.execute(context, exeLogger);
			}catch(Exception ex)
			{
				exeLogger.error(null, ex, "An error occurred while executing test case '{}' with data: {}", testCase.getName(), data);
				result = new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "An unhandled error occurred while executing test case with data: " + data);
			}finally
			{
				context.getExecutionStack().pop(testCase);
				context.clearActiveTestCase();
			}
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring();
			reportGenerator.createLogFiles(context, result, testFileName + "_" + nextFileIndex.getAndIncrement(), monitoringLogs, data + "\n" + testCase.getDescription());

			if(result.getStatus() == TestStatus.ERRORED)
			{
				finalStatus = TestStatus.ERRORED;
			}
			
			if(result.getStatus() == TestStatus.FAILED && finalStatus != TestStatus.ERRORED)
			{
				finalStatus = TestStatus.FAILED;
			}
			
			testCaseDatsResults.add(result);
		}
		
		return new TestCaseResult(testCase.getName(), finalStatus, null, "", true);
	}
	
	private TestCaseResult executeTestCaseWithDependencies(TestSuite testSuite, TestCase testCase)
	{
		//if test case execution is already in progress in 
		if(fullExecutionDetails.isTestCaseInProgress(testCase.getName()))
		{
			throw new InvalidStateException("Test case is being {} is being re-executed when it is already in progress", testCase.getName());
		}
		
		TestCaseResult depTestCaseResult = fullExecutionDetails.getTestCaseResult(testSuite.getName(), testCase.getName());

		//if test case is already executed, simply return
		if(depTestCaseResult != null)
		{
			logger.warn("Skipping test case as it is already executed- [Test suite: {}, Test Case: {}]", testSuite.getName(), testCase.getName());
			return depTestCaseResult;
		}
		
		fullExecutionDetails.startedTestCase(testCase.getName());
		
		try
		{
			Set<String> dependencyTestCases = testCase.getDependenciesSet();
			
			//Check the dependency test cases of the test case 
			if(dependencyTestCases != null)
			{
				for(String depTestCase : dependencyTestCases)
				{
					depTestCaseResult = fullExecutionDetails.getTestCaseResult(testSuite.getName(), depTestCase);
					
					if(depTestCaseResult == null)
					{
						TestCase depTestCaseObj = testSuite.getTestCase(depTestCase);
						
						if(depTestCaseObj == null)
						{
							throw new InvalidStateException("Invalid dependency test case '{}' specified for test case - {}", depTestCase, testCase.getName());
						}
						
						depTestCaseResult = executeTestCaseWithDependencies(testSuite, depTestCaseObj);
					}
					
					if(depTestCaseResult.getStatus() == TestStatus.SUCCESSFUL)
					{
						continue;
					}
					
					String skipMssg = String.format("Skipping test case '%s' as the status of dependency test case '%s' is found as - %s", 
						testCase.getName(), depTestCase, depTestCaseResult.getStatus());
					logger.info(skipMssg);
					
					TestCaseResult result = new TestCaseResult(testCase.getName(), TestStatus.SKIPPED, null, skipMssg);
					fullExecutionDetails.addTestResult(testSuite, result);
					return result;
				}
			}
			
			TestSuiteResults currentTestSuiteResults = fullExecutionDetails.getTestSuiteResults(testSuite.getName());
			
			//Execute the setup before first test case is executed.
			// Note: if no test cases are executed (due to various factors) setup will not be executed. And cleanup will also gets skipped
			if(!currentTestSuiteResults.isSetupSuccessful())
			{
				if( !executeSetup(testSuite.getName(), testSuite.getSetups()) )
				{
					TestSuiteResults results = fullExecutionDetails.testSuiteSkipped(testSuite, "Skipping as setup of test suite is failed");
					results.setSetupSuccessful(false);
					
					throw new ExecutionFailedException("Setup execution failed");
				}
				
				currentTestSuiteResults.setSetupSuccessful(true);
			}
	
			//execute the test case
			logger.debug("Executing test case '{}' in test suite - {}", testCase.getName(), testSuite.getName());
	
			String testFileName = testSuite.getName() + "_" + testCase.getName();
	
			//execute actual test case
			long testCaseExecutionId = 0, startTime = System.currentTimeMillis();
			
			//add new execution to store
			if(context.getPersistenceStorage() != null)
			{
				testCaseExecutionId = context.getPersistenceStorage().testCaseStarted(testSuite.getName(), testCase.getName());
			}
			
			List<TestCaseResult> testCaseDataResults = new ArrayList<>();
			TestCaseResult testCaseResult = executeTestCase(context, testCaseDataResults, testCase, testFileName);
			
			//Accumulated result (Accumulated of data-provider results) should not be persisted directly.
			if(!testCaseResult.isAccumlatedResult())
			{
				//update store with execution result
				if(context.getPersistenceStorage() != null)
				{
					context.getPersistenceStorage().updateExecution(testCaseExecutionId, 
							testCaseResult.getStatus() == TestStatus.SUCCESSFUL, 
							(System.currentTimeMillis() - startTime), 
							testCaseResult.getMessage());
				}
	
				fullExecutionDetails.addTestResult(testSuite, testCaseResult);
			}
			
			for(TestCaseResult dataResult : testCaseDataResults)
			{
				fullExecutionDetails.addTestResult(testSuite, dataResult);
			}
			
			return testCaseResult;
		}finally
		{
			fullExecutionDetails.closeTestCase(testCase.getName());
		}
	}

	/**
	 * Executes specified test suite and its dependencies recursively.
	 * 
	 * @param testSuite
	 *            Test suite to execute
	 * @return True if all dependencies and test cases passed.
	 */
	private boolean executeTestSuite(TestSuite testSuite)
	{
		if(fullExecutionDetails.isTestSuiteExecuted(testSuite.getName()))
		{
			return fullExecutionDetails.isTestSuiteCompleted(testSuite.getName());
		}

		logger.debug("Executing test suite - {}", testSuite.getName());
		fullExecutionDetails.testSuiteInProgress(testSuite.getName());

		this.currentTestSuite = testSuite;

		boolean successful = true;
		
		Set<String> restrictedTestCases = context.getBasicArguments().getTestCasesSet();
		boolean testCaseExecuted = false;
		
		if(restrictedTestCases != null)
		{
			logger.debug("Test cases are limited to - {}", restrictedTestCases);
		}
		
		//set test suite as active test suite
		context.setActiveTestSuite(testSuite);
		
		try
		{
			TestCaseResult testCaseResult = null;
	
			for(TestCase testCase : testSuite.getTestCases())
			{
				if(restrictedTestCases != null && !restrictedTestCases.contains(testCase.getName()))
				{
					continue;
				}

				testCaseExecuted = true;
				testCaseResult = executeTestCaseWithDependencies(testSuite, testCase);

				if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
				{
					successful = false;
				}
			}
			
			TestSuiteResults currentTestSuiteResults = fullExecutionDetails.getTestSuiteResults(testSuite.getName());
			
			//execute the cleanup only if setup is executed. 
			// Note: setup will be executed only if atleast one test case is executed in current test suite
			if(currentTestSuiteResults.isSetupSuccessful())
			{
				if( !executeCleanup(testSuite.getName(), testSuite.getCleanups()) )
				{
					fullExecutionDetails.testSuiteFailed(testSuite, "Failing as cleanup of test suite is failed");
					return false;
				}
			}
		}finally
		{
			context.clearActiveTestSuite();
		}
		
		if(!testCaseExecuted)
		{
			fullExecutionDetails.removeTestSuite(testSuite);
			return successful;
		}

		if(successful)
		{
			fullExecutionDetails.testSuiteCompleted(testSuite);
		}
		else
		{
			fullExecutionDetails.testSuiteFailed(testSuite, "Failing as one or more test cases failed");
		}

		return successful;
	}
	
	/**
	 * Executes the setup steps.
	 */
	private boolean executeSetup(String prefix, List<Setup> setups)
	{
		if(CollectionUtils.isEmpty(setups))
		{
			logger.debug("No {} setup steps found, ignoring global setup execution.", prefix);
			return true;
		}
		
		for(Setup setup : setups)
		{
			logger.debug("Executing {} setup steps specified at location: {}", prefix, setup.getLocation());
			
			TestCaseResult testCaseResult = setup.execute(context);
			reportGenerator.createLogFiles(context, testCaseResult, prefix + "-setup", null, "");
			
			if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
			{
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Executes the cleanup steps.
	 */
	private boolean executeCleanup(String prefix, List<Cleanup> cleanups)
	{
		if(context.getInteractiveEnvironmentContext() != null)
		{
			logger.debug("As the execution is part of interactive environment, skipping cleanup of: {}", prefix);
			return true;
		}
		
		if(CollectionUtils.isEmpty(cleanups))
		{
			logger.debug("No {} cleanup steps found, ignoring global cleanup execution.", prefix);
			return true;
		}
		
		logger.debug("Executing {} cleanup steps...", prefix);
		
		for(Cleanup cleanup : cleanups)
		{
			TestCaseResult testCaseResult = cleanup.execute(context);
			reportGenerator.createLogFiles(context, testCaseResult, prefix + "-cleanup", null, "");
			
			if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Executes global setups.
	 * @return true if successfully executed.
	 */
	public boolean executeGlobalSetup()
	{
		if(context.getInteractiveEnvironmentContext() != null && !context.getInteractiveEnvironmentContext().isExecuteGlobalSetup())
		{
			return true;
		}
		
		if(testSuiteGroup.getSetup() == null)
		{
			return true;
		}
		
		return executeSetup("_global", Arrays.asList(testSuiteGroup.getSetup()));
	}

	/**
	 * Executes test suites with specified context.
	 * 
	 */
	public boolean executeTestSuites()
	{
		boolean successful = true;
		
		if(! executeGlobalSetup() )
		{
			logger.error("Skipping all test suite execution as global setup failed.");
			fullExecutionDetails.setSetupSuccessful(false);
			
			successful = false;
		}
		
		//if setup was not successful dont execute any test suites
		if(successful)
		{
			BasicArguments basicArguments = context.getBasicArguments();
			
			Set<String> limitedTestSuites = basicArguments.getTestSuitesSet();

			// if limited test suites are specified, only execute them and their
			// dependencies
			if(CollectionUtils.isNotEmpty(limitedTestSuites))
			{
				logger.debug("Executing limited test suites - {}", limitedTestSuites);
	
				TestSuite testSuite = null;
	
				for(String name : limitedTestSuites)
				{
					testSuite = testSuiteGroup.getTestSuite(name);
	
					if(testSuite == null)
					{
						throw new InvalidConfigurationException("Invalid test suite name '{}' specified in limited test suites", name);
					}
	
					if( !executeTestSuite(testSuite) )
					{
						successful = false;
					}
				}
			}
			// if no limited test suites are specified execute all test suites
			else
			{
				for(TestSuite testSuite : testSuiteGroup.getTestSuites())
				{
					if( !executeTestSuite(testSuite) )
					{
						successful = false;
					}
				}
			}
		}
		
		if( testSuiteGroup.getCleanup() != null && !executeCleanup("_global", Arrays.asList(testSuiteGroup.getCleanup())) )
		{
			logger.error("Global cleanup failed.");
			fullExecutionDetails.setCleanupSuccessful(false);
			
			successful = false;
		}
		
		if(context.getInteractiveEnvironmentContext() != null)
		{
			logger.debug("As this is interactive environment, skipping the report generation");
			return successful;
		}
		
		//create final report files
		reportGenerator.generateReports(reportFolder, fullExecutionDetails, context);
		
		System.out.println(String.format(
			"[Total Test Cases: %s, Successful: %s, Failed: %s, Errored: %s, Skipped: %s]", 
			fullExecutionDetails.getTestCaseCount(), 
			fullExecutionDetails.getTestCaseSuccessCount(), 
			fullExecutionDetails.getTestCaseFailureCount(),
			fullExecutionDetails.getTestCaseErroredCount(),
			fullExecutionDetails.getTestCaseSkippedCount()
		));
		
		return successful;
	}
}
