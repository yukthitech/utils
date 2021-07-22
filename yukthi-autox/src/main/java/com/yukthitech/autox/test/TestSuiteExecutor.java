package com.yukthitech.autox.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.Command;
import com.yukthitech.autox.event.AutomationEvent;
import com.yukthitech.utils.ObjectWrapper;
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
	public TestCaseResult executeTestCase(AutomationContext context, List<TestCaseResult> testCaseDataResults, TestCase testCase, 
			String testFileName, TestSuite testSuite)
	{
		ObjectWrapper<String> excludedGrp = new ObjectWrapper<>();
		Date startTime = new Date();
		
		if(!testCase.isExecutable(context, excludedGrp))
		{
			return new TestCaseResult(testCase, TestStatus.SKIPPED, null, 
					"Skipping as the test case group '" + excludedGrp.getValue() + "' is under exclusion list.",
					startTime, new Date());
		}

		IDataProvider dataProvider = testCase.getDataProvider();
		
		if(dataProvider == null)
		{
			ExecutionLogger exeLogger = new ExecutionLogger(context, testCase.getName(), testCase.getDescription());
			TestCaseResult result = null;
			
			context.getAutomationListener().testCaseStarted(new AutomationEvent(currentTestSuite, testCase, null));
			
			//start monitoring logs
			context.startLogMonitoring();
			context.setActiveTestCase(testCase, null);
			
			if(context.getInteractiveEnvironmentContext() != null)
			{
				context.getInteractiveEnvironmentContext().setLastTestCase(testCase);
			}
			
			testCase.setData(null);
			context.getExecutionStack().push(testCase);
			
			try
			{
				if(testSuite.getBeforeTestCase() != null)
				{
					if(!executeSetup("before-test-case", Arrays.asList(testSuite.getBeforeTestCase()), exeLogger))
					{
						result = new TestCaseResult(testCase, TestStatus.SKIPPED, null, "Skipping the test case as before-test-case of test-suite failed.",
								startTime, new Date());
					}
				}
				
				//execute only when before-test-case was successful
				if(result == null)
				{
					result = testCase.execute(context, exeLogger);
				}
			}catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing test case: {}", testCase.getName());
				result = new TestCaseResult(testCase, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
						"An unhandled error occurred while executing test case.",
						startTime, new Date());
			} finally
			{
				if(testSuite.getAfterTestCase() != null)
				{
					try
					{
						if(!executeCleanup("after-test-case", Arrays.asList(testSuite.getAfterTestCase()), exeLogger))
						{
							result = new TestCaseResult(testCase, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
									"Failed to execute after-test-case of test-suite.",
									startTime, new Date());
						}
					}catch(Exception ex)
					{
						exeLogger.error(ex, "An error occurred while executing after-test-case of test-suited: {}", testCase.getName());
						result = new TestCaseResult(testCase, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
								"Failed to execute after-test-case of test-suite.",
								startTime, new Date());
					}
				}

				context.getExecutionStack().pop(testCase);
				context.clearActiveTestCase();
			}
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring(result);
			reportGenerator.createLogFiles(context, result, testFileName, monitoringLogs, testCase.getDescription());
			
			context.getAutomationListener().testCaseCompleted(new AutomationEvent(currentTestSuite, testCase, result));
			
			return result;
		}
		
		TestCaseResult result = null;
		ExecutionLogger exeLogger = null;
		String name = null;
		String description = null;
		TestStatus finalStatus = TestStatus.SUCCESSFUL;
		
		Setup dataSetup = testCase.getDataSetup();
		Cleanup dataCleanup = testCase.getDataCleanup();
		int dataIndex = 0;
		
		//Execute the data-setup if any and generate the log
		if(dataSetup != null)
		{
			name = testCase.getName() + " [[Data-Setup]]";
			
			logger.debug("Executing data-setup steps for test case: {}", name);
			exeLogger = new ExecutionLogger(context, name, "Data setup");
			
			result = dataSetup.execute(context, exeLogger);
			
			if(result.getStatus() != TestStatus.SUCCESSFUL)
			{
				result = new TestCaseResult(testCase, name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Data-setup execution failed.",
						startTime, new Date());
			}
			
			reportGenerator.createLogFiles(context, result, testFileName + "_" + nextFileIndex.getAndIncrement(), null, testCase.getName() + "\n[[Data-Setup]]");
			
			if(result.getStatus() != TestStatus.SUCCESSFUL)
			{
				return result;
			}
			
			result.setTestCaseName(name);
			testCaseDataResults.add(result);
		}

		List<TestCaseData> dataLst = null;
		context.setActiveTestCase(testCase, null);
		
		try
		{
			dataLst = dataProvider.getStepData();
		} finally
		{
			context.clearActiveTestCase();
		}

		//Log error and fail test case if data is missing
		if(dataLst == null)
		{
			return new TestCaseResult(testCase, TestStatus.ERRORED, null, "No data from data-provider. Data Provider: " + dataProvider.getName(),
					startTime, new Date());
		}

		for(TestCaseData data : dataLst)
		{
			dataIndex++;

			name = testCase.getName() + " [" + data.getName() + "]";
			description = data.getDescription();
			description = StringUtils.isNotBlank(description)? description : testCase.getDescription(); 
			
			exeLogger = new ExecutionLogger(context, name, description);
			exeLogger.debug("Executing test case '{}' with data: {}", name, data.getName());

			//start monitoring logs
			context.startLogMonitoring();
			context.setActiveTestCase(testCase, data);
			testCase.setData(data);
			
			//set data provider data on context
			context.setAttribute(dataProvider.getName(), data.getValue());

			context.getExecutionStack().push(testCase);
			
			result = null;

			//execute test case for current data
			if(testSuite.getBeforeTestCase() != null)
			{
				if(!executeSetup("before-test-case", Arrays.asList(testSuite.getBeforeTestCase()), exeLogger))
				{
					result = new TestCaseResult(testCase, TestStatus.SKIPPED, null, "Skipping the test case as before-test-case of test-suite failed.",
							startTime, new Date());
				}
			}

			try
			{
				//execute only when before-test-case is successful
				if(result == null)
				{
					result = testCase.execute(context, exeLogger);
				}
			}catch(Exception ex)
			{
				exeLogger.error(null, ex, "An error occurred while executing test case '{}' with data: {}", testCase.getName(), data);
				result = new TestCaseResult(testCase, name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
						"An unhandled error occurred while executing test case with data: " + data,
						startTime, new Date());
			}finally
			{
				if(testSuite.getAfterTestCase() != null)
				{
					try
					{
						if(!executeCleanup("after-test-case", Arrays.asList(testSuite.getAfterTestCase()), exeLogger))
						{
							result = new TestCaseResult(testCase, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
									"Failed to execute after-test-case of test-suite.",
									startTime, new Date());
						}
					}catch(Exception ex)
					{
						exeLogger.error(ex, "An error occurred while executing after-test-case of test-suited: {}", testCase.getName());
						result = new TestCaseResult(testCase, TestStatus.ERRORED, exeLogger.getExecutionLogData(), 
								"Failed to execute after-test-case of test-suite.",
								startTime, new Date());
					}
				}

				context.getExecutionStack().pop(testCase);
				context.clearActiveTestCase();
			}
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring(result);
			reportGenerator.createLogFiles(context, result, testFileName + "_" + nextFileIndex.getAndIncrement(), monitoringLogs, data + "\n" + testCase.getDescription());

			if(result.getStatus() == TestStatus.ERRORED)
			{
				finalStatus = TestStatus.ERRORED;
			}
			
			if(result.getStatus() == TestStatus.FAILED && finalStatus != TestStatus.ERRORED)
			{
				finalStatus = TestStatus.FAILED;
			}
			
			testCaseDataResults.add(result);

			//execute data cleanup as part of last data test case
			if(dataIndex == dataLst.size() && dataCleanup != null)
			{
				logger.debug("Executing data-cleanup steps for test case: {}", name);
				exeLogger = new ExecutionLogger(context, testCase.getName() + " [[Data-Cleanup]]", "Data Cleanup");
				
				result = dataCleanup.execute(context, exeLogger);
				result.setTestCaseName(testCase.getName() + " [[Data-Cleanup]]");
				
				reportGenerator.createLogFiles(context, result, testFileName + "_" + nextFileIndex.getAndIncrement(), monitoringLogs, testCase.getName() + "\n[[Data-Cleanup]]");
				
				if(result.getStatus() != TestStatus.SUCCESSFUL)
				{
					finalStatus = TestStatus.FAILED;
				}
				
				testCaseDataResults.add(result);
			}
		}

		return new TestCaseResult(testCase, finalStatus, null, "", true,
				startTime, new Date());
	}
	
	/**
	 * Executes the dependencies of specified test case if any. And also setup of test-suite if any. 
	 * @param testSuite parent test suite
	 * @param testCase test case whose dependencies needs to be checked
	 * @return null if all dependencies are executed successfully. If not, returns the result the test case execution should return.
	 */
	private TestCaseResult executeDependencies(TestSuite testSuite, TestCase testCase)
	{
		Set<String> dependencyTestCases = testCase.getDependenciesSet();
		TestCaseResult depTestCaseResult = null;
		
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
						String skipMssg = String.format("Invalid dependency test case '%s' specified for test case - %s", 
								depTestCase, testCase.getName());
						logger.info(skipMssg);
							
						TestCaseResult result = new TestCaseResult(testCase, TestStatus.SKIPPED, null, skipMssg, null, null);
						fullExecutionDetails.addTestResult(testSuite, result);
						return result;
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
				
				TestCaseResult result = new TestCaseResult(testCase, TestStatus.SKIPPED, null, skipMssg, null, null);
				fullExecutionDetails.addTestResult(testSuite, result);
				return result;
			}
		}
		
		TestSuiteResults currentTestSuiteResults = fullExecutionDetails.getTestSuiteResults(testSuite.getName());
		
		//Execute the setup before first test case is executed.
		// Note: if no test cases are executed (due to various factors) setup will not be executed. And cleanup will also gets skipped
		if(!currentTestSuiteResults.isSetupSuccessful())
		{
			if( !executeSetup(testSuite.getName(), testSuite.getSetups(), null) )
			{
				TestSuiteResults results = fullExecutionDetails.testSuiteSkipped(testSuite, "Skipping as setup of test suite is failed");
				results.setSetupSuccessful(false);
				
				throw new SetupExecutionFailedException("Setup execution failed");
			}
			
			currentTestSuiteResults.setSetupSuccessful(true);
		}

		return null;
	}
	private TestCaseResult executeTestCaseWithDependencies(TestSuite testSuite, TestCase testCase)
	{
		//if test case execution is already in progress in 
		if(fullExecutionDetails.isTestCaseInProgress(testCase.getName()))
		{
			throw new InvalidStateException("Test case {} is being re-executed when it is already in progress", testCase.getName());
		}
		
		TestCaseResult curTestCaseResult = fullExecutionDetails.getTestCaseResult(testSuite.getName(), testCase.getName());

		//if test case is already executed, simply return
		if(curTestCaseResult != null)
		{
			logger.warn("Skipping test case as it is already executed- [Test suite: {}, Test Case: {}]", testSuite.getName(), testCase.getName());
			return curTestCaseResult;
		}
		
		curTestCaseResult = executeDependencies(testSuite, testCase);
		
		if(curTestCaseResult != null)
		{
			logger.warn("Skipping actual test case execution as the dependencies execution was not successful. [Test suite: {}, Test Case: {}]", testSuite.getName(), testCase.getName());
			return curTestCaseResult;
		}
	
		fullExecutionDetails.startedTestCase(testCase.getName());
		
		try
		{
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
			TestCaseResult testCaseResult = executeTestCase(context, testCaseDataResults, testCase, testFileName, testSuite);
			
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
			//in case of non-accumulated result also, add the accumulated result
			// so that dependencies test cases can refer to this result
			else
			{
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
		fullExecutionDetails.testSuiteInProgress(testSuite);

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

				//for interactive environment set the test suite on interactive context
				// last test suite to be set when testcase is being executed (thats why it is kept inside loop)
				if(context.getInteractiveEnvironmentContext() != null)
				{
					context.getInteractiveEnvironmentContext().setLastTestSuite(testSuite);
				}

				testCaseExecuted = true;
				testCaseResult = executeTestCaseWithDependencies(testSuite, testCase);

				if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL && testCaseResult.getStatus() != TestStatus.SKIPPED)
				{
					successful = false;
				}
			}
			
			TestSuiteResults currentTestSuiteResults = fullExecutionDetails.getTestSuiteResults(testSuite.getName());
			
			//execute the cleanup only if setup is executed. 
			// Note: setup will be executed only if atleast one test case is executed in current test suite
			if(currentTestSuiteResults.isSetupSuccessful())
			{
				if( !executeCleanup(testSuite.getName(), testSuite.getCleanups(), null) )
				{
					currentTestSuiteResults.setCleanupSuccessful(false);
					fullExecutionDetails.testSuiteFailed(testSuite, "Cleanup execution failed.");
					return false;
				}
			}
			
			currentTestSuiteResults.setCleanupSuccessful(true);
		}catch(SetupExecutionFailedException ex)
		{
			logger.error("Setup execution failed. Marking the test suite as failed.", ex);
			fullExecutionDetails.testSuiteFailed(testSuite, "Setup execution failed.");
			return false;
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
	private boolean executeSetup(String prefix, List<Setup> setups, ExecutionLogger exeLogger)
	{
		if(CollectionUtils.isEmpty(setups))
		{
			logger.debug("No {} setup steps found, ignoring global setup execution.", prefix);
			return true;
		}

		try
		{
			for(Setup setup : setups)
			{
				logger.debug("Executing {} setup steps specified at location: {}", prefix, setup.getLocation());
				
				TestCaseResult testCaseResult = null;
						
				if(exeLogger == null)
				{
					testCaseResult = setup.execute(context);
					reportGenerator.createLogFiles(context, testCaseResult, prefix + "-setup", null, "");
				}
				else
				{
					testCaseResult = setup.execute(context, exeLogger);
				}
				
				if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
				{
					return false;
				}
			}
		}catch(Exception ex)
		{
			logger.error("An error occurred while execute global setup", ex);
			return false;
		}
		
		return true;
	}

	/**
	 * Executes the cleanup steps.
	 */
	private boolean executeCleanup(String prefix, List<Cleanup> cleanups, ExecutionLogger exeLogger)
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
		
		try
		{
			for(Cleanup cleanup : cleanups)
			{
				TestCaseResult testCaseResult = null;
				
				if(exeLogger == null)
				{
					testCaseResult = cleanup.execute(context);
					reportGenerator.createLogFiles(context, testCaseResult, prefix + "-cleanup", null, "");
				}
				else
				{
					testCaseResult = cleanup.execute(context, exeLogger);
				}
				
				if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
				{
					return false;
				}
			}
		}catch(Exception ex)
		{
			logger.error("An error occurred while execute global cleanup", ex);
			return false;
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
		
		return executeSetup("_global", Arrays.asList(testSuiteGroup.getSetup()), null);
	}

	/**
	 * Executes test suites with specified context.
	 * 
	 */
	public boolean executeTestSuites()
	{
		boolean successful = true;
		this.fullExecutionDetails.setStartTime(new Date());
		
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
						throw new InvalidConfigurationException("Invalid test suite name '{}' specified for execution", name);
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
				ApplicationConfiguration appConfig = context.getAppConfiguration();
				
				for(TestSuite testSuite : testSuiteGroup.getTestSuites())
				{
					if(appConfig.isTestSuiteExcluded(testSuite.getName()))
					{
						continue;
					}
					
					if( !executeTestSuite(testSuite) )
					{
						successful = false;
					}
				}
			}
		}
		
		if( testSuiteGroup.getCleanup() != null && !executeCleanup("_global", Arrays.asList(testSuiteGroup.getCleanup()), null) )
		{
			logger.error("Global cleanup failed.");
			fullExecutionDetails.setCleanupSuccessful(false);
			
			successful = false;
		}
		
		this.fullExecutionDetails.setEndTime(new Date());
		
		if(context.getInteractiveEnvironmentContext() != null)
		{
			logger.debug("As this is interactive environment, skipping the report generation");
			return successful;
		}
		
		//execute post commands if any
		List<Command> postCommands = context.getAppConfiguration().getPostCommands();
		
		for(Command command : postCommands)
		{
			logger.debug("Executing post-command: {}", command.getName());
			
			command.execute(context, new Command.ICommandLogger()
			{
				@Override
				public void warn(String mssg, Object... args)
				{
					logger.warn(mssg, args);
				}
				
				@Override
				public void info(String mssg, Object... args)
				{
					logger.info(mssg, args);
				}
				
				@Override
				public void debug(String mssg, Object... args)
				{
					logger.debug(mssg, args);
				}
				
				@Override
				public void output(String line)
				{
					logger.debug("[CMDLOG] [{}] - {}", command.getName(), line);
				}
			});
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
