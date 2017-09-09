package com.yukthitech.autox.test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.event.AutomationEvent;
import com.yukthitech.autox.logmon.LogMonitorContext;
import com.yukthitech.autox.test.log.ExecutionLogData;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Test suites executors.
 * @author akiran
 */
public class TestSuiteExecutor
{
	private static Logger logger = LogManager.getLogger(TestSuiteExecutor.class);
	
	/**
	 * Free marker configuration.
	 */
	private static Configuration freemarkerConfiguration = new Configuration(Configuration.getVersion());
	
	/**
	 * The log json file extension.
	 **/
	private static String LOG_JSON = "_log.json";
	
	/**
	 * log js file extension.
	 */
	private static String LOG_JS = ".js";
	
	/**
	 * Used to generate json files.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
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
	 * Logs folder where logs should be maintained.
	 */
	private File logsFolder;
	
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
	 * @param reportFolder the report folder
	 */
	public TestSuiteExecutor(AutomationContext context, TestSuiteGroup testSuiteGroup, File reportFolder)
	{
		this.context = context;
		this.testSuiteGroup = testSuiteGroup;
		this.reportFolder = reportFolder;

		// create logs folder
		logsFolder = new File(reportFolder, "logs");
		logsFolder.mkdirs();
		
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
			ExecutionLogger exeLogger = new ExecutionLogger(testCase.getName(), testCase.getDescription());
			TestCaseResult result = null;
			
			context.getAutomationListener().testCaseStarted(new AutomationEvent(currentTestSuite, testCase, null));
			
			//start monitoring logs
			context.startLogMonitoring();

			try
			{
				result = testCase.execute(context, null, exeLogger);
			}catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing test case: {}", testCase.getName());
				result = new TestCaseResult(testCase.getName(), TestStatus.ERRORED, exeLogger.getExecutionLogData(), "An unhandled error occurred while executing test case.");
			} 
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring();
			createLogFiles(result, testFileName, monitoringLogs, testCase.getDescription());
			
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
			exeLogger = new ExecutionLogger(name, testCase.getDescription());
			
			exeLogger.debug("Executing test case '{}' with data: {}", name, data.getName());
					
			//set data provider data on context
			context.setAttribute(dataProvider.getName(), data.getValue());

			//start monitoring logs
			context.startLogMonitoring();

			//execute test case for current data
			try
			{
				result = testCase.execute(context, data, exeLogger);
			}catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing test case '{}' with data: {}", testCase.getName(), data);
				result = new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "An unhandled error occurred while executing test case with data: " + data);
			}
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring();
			createLogFiles(result, testFileName + "_" + nextFileIndex.getAndIncrement(), monitoringLogs, data + "\n" + testCase.getDescription());

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
		
		//return new TestCaseResult(testCase.getName(), finalStatus, null, "");
		return null;
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

		// if test suite has dependencies execute them first
		if(testSuite.getDependencies() != null)
		{
			TestSuite depTestSuite = null;

			for(String dependencyTestSuite : testSuite.getDependencies())
			{
				// if dependency is already completed, ignore
				if(fullExecutionDetails.isTestSuiteCompleted(dependencyTestSuite))
				{
					continue;
				}

				// if dependency is failed, skip the test case
				if(fullExecutionDetails.isTestSuiteFailed(dependencyTestSuite))
				{
					fullExecutionDetails.testSuiteSkipped(testSuite, "Skipping as dependency test suite is failed/skipped - " + dependencyTestSuite);
					return false;
				}

				// if dependency is already in progress, then it is recursion,
				// throw error
				if(fullExecutionDetails.isTestSuiteInProgress(dependencyTestSuite))
				{
					throw new InvalidStateException("Encountered circular dependency with '{}' in test suite - {}", depTestSuite, testSuite.getName());
				}

				depTestSuite = testSuiteGroup.getTestSuite(dependencyTestSuite);

				if( !executeTestSuite(depTestSuite) )
				{
					fullExecutionDetails.testSuiteSkipped(testSuite, "Failed as the dependency test-suite '" + depTestSuite + "' failed.");
					return false;
				}
			}
		}
		
		this.currentTestSuite = testSuite;

		TestCaseResult testCaseResult = null;
		List<TestCaseResult> testCaseDataResults = new ArrayList<>();
		String testFileName = null;

		boolean successful = true;
		
		Set<String> restrictedTestCases = context.getBasicArguments().getTestCasesSet();
		
		if(restrictedTestCases != null)
		{
			logger.debug("Test cases are limited to - {}", restrictedTestCases);
		}
		
		Set<String> dependencyTestCases = null;
		TestCaseResult depTestCaseResult = null;

		if( !executeSetup(testSuite.getName(), testSuite.getSetup()) )
		{
			TestSuiteResults results = fullExecutionDetails.testSuiteSkipped(testSuite, "Skipping as setup of test suite is failed");
			results.setSetupFailed(true);
			
			return false;
		}
		
		TEST_CASE_LOOP: for(TestCase testCase : testSuite.getTestCases())
		{
			if(restrictedTestCases != null && !restrictedTestCases.contains(testCase.getName()))
			{
				continue;
			}
			
			dependencyTestCases = testCase.getDependenciesSet();
			
			//Check the dependency test cases of the test case 
			if(dependencyTestCases != null)
			{
				for(String depTestCase : dependencyTestCases)
				{
					depTestCaseResult = fullExecutionDetails.getTestCaseResult(testSuite.getName(), depTestCase);
					
					if(depTestCaseResult == null)
					{
						logger.warn("Ignoring invalid dependency '{}' of test case - {}", depTestCase, testCase.getName());
						continue;
					}
					
					if(depTestCaseResult.getStatus() == TestStatus.SUCCESSFUL)
					{
						continue;
					}
					
					String skipMssg = String.format("Skipping test case '%s' as the status of dependency test case '%s' is found as - %s", 
						testCase.getName(), depTestCase, depTestCaseResult.getStatus());
					logger.info(skipMssg);
					
					fullExecutionDetails.addTestResult(testSuite, new TestCaseResult(testCase.getName(), TestStatus.SKIPPED, null, skipMssg));
					
					continue TEST_CASE_LOOP;
				}
			}
			
			//execute the test case
			logger.debug("Executing test case '{}' in test suite - {}", testCase.getName(), testSuite.getName());

			testFileName = testSuite.getName() + "_" + testCase.getName();

			//execute actual test case
			testCaseDataResults.clear();
			testCaseResult = executeTestCase(context, testCaseDataResults, testCase, testFileName);
			
			//test case result can be null, in case of data provider
			if(testCaseResult != null)
			{
				if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
				{
					successful = false;
				}
	
				fullExecutionDetails.addTestResult(testSuite, testCaseResult);
			}
			
			for(TestCaseResult dataResult : testCaseDataResults)
			{
				fullExecutionDetails.addTestResult(testSuite, dataResult);
			}
		}
		
		if( !executeCleanup(testSuite.getName(), testSuite.getCleanup()) )
		{
			fullExecutionDetails.testSuiteFailed(testSuite, "Failing as cleanup of test suite is failed");
			return false;
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
	 * Creates log files from specified test case result.
	 * @param testCaseResult result from which log data needs to be fetched
	 * @param logFilePrefix log file name prefix
	 * @param monitoringLogs Monitoring logs to be copied
	 * @param description Description about the test case.
	 */
	private void createLogFiles(TestCaseResult testCaseResult, String logFilePrefix, Map<String, File> monitoringLogs, String description)
	{
		ExecutionLogData executionLogData = testCaseResult.getExecutionLog();
		
		if( executionLogData == null)
		{
			return;
		}
		
		executionLogData.setStatus(testCaseResult.getStatus());
		
		executionLogData.copyResources(logsFolder);
		
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(executionLogData);
			String jsContent = "var logData = " + jsonContent;
			
			FileUtils.write(new File(logsFolder, logFilePrefix + LOG_JSON), jsonContent);
			FileUtils.write(new File(logsFolder, logFilePrefix + LOG_JS), jsContent);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating test log json file - {}", new File(logsFolder, logFilePrefix + LOG_JSON));
		}

		if(monitoringLogs != null)
		{
			for(Map.Entry<String, File> log : monitoringLogs.entrySet())
			{
				if(log.getValue() == null)
				{
					continue;
				}
				
				try
				{
					FileUtils.copyFile(log.getValue(), new File(logsFolder, logFilePrefix + "_" + log.getKey() + ".log"));
					
					Template freemarkerTemplate = new Template("monitor-log-template", 
							new InputStreamReader(AutomationLauncher.class.getResourceAsStream("/monitor-log-template.html")), freemarkerConfiguration);

					File logHtmlFile = new File(logsFolder, logFilePrefix + "_" + log.getKey() + ".log.html");
					FileWriter writer = new FileWriter(logHtmlFile);
					String logContent = FileUtils.readFileToString(log.getValue());
					
					freemarkerTemplate.process(new LogMonitorContext(testCaseResult.getTestCaseName(), log.getKey(), logContent, testCaseResult.getStatus(), description), writer);

					testCaseResult.setMonitorLog(log.getKey(), logHtmlFile.getName());
					
					writer.flush();
					writer.close();
					
					log.getValue().delete();
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while creating monitoring log file - {}", log.getKey());
				}
			}
		}
		
		testCaseResult.setSystemLogName(logFilePrefix);
	}
	
	/**
	 * Executes the setup steps.
	 */
	private boolean executeSetup(String prefix, Setup setup)
	{
		if(setup == null)
		{
			logger.debug("No {} setup steps found, ignoring global setup execution.", prefix);
			return true;
		}
		
		logger.debug("Executing {} setup steps...", prefix);
		
		TestCaseResult testCaseResult = setup.execute(context);
		createLogFiles(testCaseResult, prefix + "-setup", null, "");
		
		return testCaseResult.getStatus() == TestStatus.SUCCESSFUL;
	}

	/**
	 * Executes the cleanup steps.
	 */
	private boolean executeCleanup(String prefix, Cleanup cleanup)
	{
		if(cleanup == null)
		{
			logger.debug("No {} cleanup steps found, ignoring global cleanup execution.", prefix);
			return true;
		}
		
		logger.debug("Executing {} cleanup steps...", prefix);
		
		TestCaseResult testCaseResult = cleanup.execute(context);
		createLogFiles(testCaseResult, prefix + "-cleanup", null, "");
		
		return testCaseResult.getStatus() == TestStatus.SUCCESSFUL;
	}

	/**
	 * Executes test suites with specified context.
	 * 
	 */
	public boolean executeTestSuites()
	{
		boolean successful = true;
		
		if(! executeSetup("_global", testSuiteGroup.getSetup()) )
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
		
		if( !executeCleanup("_global", testSuiteGroup.getCleanup()) )
		{
			logger.error("Global cleanup failed.");
			fullExecutionDetails.setCleanupSuccessful(false);
			
			successful = false;
		}
		
		//create final report files
		reportGenerator.generateReports(reportFolder, fullExecutionDetails, context.getAppConfiguration());
		
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
