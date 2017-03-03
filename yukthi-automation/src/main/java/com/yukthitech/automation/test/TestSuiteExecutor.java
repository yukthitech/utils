package com.yukthitech.automation.test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.AutomationLauncher;
import com.yukthitech.automation.BasicArguments;
import com.yukthitech.automation.logmon.LogMonitorContext;
import com.yukthitech.automation.test.log.ExecutionLogData;
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
	 * Free marker configuraton.
	 */
	private static Configuration freemarkerConfiguration = new Configuration();
	
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
	 * Holds execution details.
	 */
	private FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
	
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

				executeTestSuite(depTestSuite);
			}
		}

		TestCaseResult testCaseResult = null;
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
			fullExecutionDetails.testSuiteSkipped(testSuite, "Skipping as setup of test suite is failed");
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

			//start monitoring logs
			context.startLogMonitoring();
			
			//execute actual test case
			testCaseResult = testCase.execute(context);
			
			//stop monitoring logs
			Map<String, File> monitoringLogs = context.stopLogMonitoring();

			if(testCaseResult.getStatus() != TestStatus.SUCCESSFUL)
			{
				successful = false;
			}

			createLogFiles(testCaseResult, testFileName, monitoringLogs);

			fullExecutionDetails.addTestResult(testSuite, testCaseResult);

			if(!successful)
			{
				break;
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
	 */
	private void createLogFiles(TestCaseResult testCaseResult, String logFilePrefix, Map<String, File> monitoringLogs)
	{
		ExecutionLogData executionLogData = testCaseResult.getExecutionLog();
		
		if( executionLogData == null)
		{
			return;
		}
		
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
					
					freemarkerTemplate.process(new LogMonitorContext(testCaseResult.getTestCaseName(), log.getKey(), logContent), writer);

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
		createLogFiles(testCaseResult, prefix + "-setup", null);
		
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
		createLogFiles(testCaseResult, prefix + "-cleanup", null);
		
		return testCaseResult.getStatus() == TestStatus.SUCCESSFUL;
	}

	/**
	 * Executes test suites with specified context.
	 * 
	 * @param context
	 *            Context to be used for automation.
	 * @param testSuiteGroup
	 *            Test suites to execute.
	 * @param reportFolder
	 *            Folder where output report needs to be generated
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
		
		//copy the resource files into output folder
		try
		{
			FileUtils.copyDirectory(new File("." + File.separator + "report-resources"), reportFolder);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while copying resource files to report folder - {}", reportFolder.getPath());
		}

		//create final report files
		try
		{
			String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullExecutionDetails);
			String jsContent = "var reportData = " + jsonContent;
			
			FileUtils.write(new File(reportFolder, "test-results.json"), jsonContent);
			FileUtils.write(new File(reportFolder, "test-results.js"), jsContent);
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating test result report");
		}
		
		return successful;
	}
}
