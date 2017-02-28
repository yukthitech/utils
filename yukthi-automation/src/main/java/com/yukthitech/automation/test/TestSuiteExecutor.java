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
	 * The log.
	 **/
	private static String JSON = ".json";
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Application context to be used.
	 */
	private AutomationContext context;
	
	/**
	 * Test suites to be executed.
	 */
	private Map<String, TestSuite> testSuiteMap;
	
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
	 * @param testSuiteMap the test suite map
	 * @param reportFolder the report folder
	 */
	public TestSuiteExecutor(AutomationContext context, Map<String, TestSuite> testSuiteMap, File reportFolder)
	{
		this.context = context;
		this.testSuiteMap = testSuiteMap;
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
		if(context.isTestSuiteExecuted(testSuite.getName()))
		{
			return context.isTestSuiteCompleted(testSuite.getName());
		}

		logger.debug("Executing test suite - {}", testSuite.getName());
		context.testSuiteInProgress(testSuite.getName());

		// if test suite has dependencies execute them first
		if(testSuite.getDependencies() != null)
		{
			TestSuite depTestSuite = null;

			for(String dependencyTestSuite : testSuite.getDependencies())
			{
				// if dependency is already completed, ignore
				if(context.isTestSuiteCompleted(dependencyTestSuite))
				{
					continue;
				}

				// if dependency is failed, skip the test case
				if(context.isTestSuiteFailed(dependencyTestSuite))
				{
					context.testSuiteFailed(testSuite.getName());
					testSuite.setStatus(TestSuiteStatus.SKIPPED);
					testSuite.setStatusMessage("Skipping as dependency test suite is failed/skipped - " + dependencyTestSuite);

					return false;
				}

				// if dependency is already in progress, then it is recursion,
				// throw error
				if(context.isTestSuiteInProgress(dependencyTestSuite))
				{
					throw new InvalidStateException("Encountered circular dependency with '{}' in test suite - {}", depTestSuite, testSuite.getName());
				}

				depTestSuite = testSuiteMap.get(dependencyTestSuite);

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
					
					if(depTestCaseResult.getStatus() == TestStatus.SUCCESSUFUL)
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

			testCaseResult = testCase.execute(context);

			if(testCaseResult.getStatus() != TestStatus.SUCCESSUFUL)
			{
				successful = false;
			}
			
			if( testCaseResult.getExecutionLog() != null)
			{
				testCaseResult.getExecutionLog().copyResources(logsFolder);
				
				try
				{
					objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(logsFolder, testFileName + JSON), testCaseResult.getExecutionLog());
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while creating test log json file - {}", new File(logsFolder, testFileName + JSON));
				}
			}

			fullExecutionDetails.addTestResult(testSuite, testCaseResult);

			if(!successful)
			{
				break;
			}
		}

		if(successful)
		{
			testSuite.setStatus(TestSuiteStatus.SUCCESSFUL);
			context.testSuiteCompleted(testSuite.getName());
		}
		else
		{
			testSuite.setStatus(TestSuiteStatus.FAILED);
			context.testSuiteFailed(testSuite.getName());
		}

		return successful;
	}

	/**
	 * Executes test suites with specified context.
	 * 
	 * @param context
	 *            Context to be used for automation.
	 * @param testSuiteMap
	 *            Test suites to execute.
	 * @param reportFolder
	 *            Folder where output report needs to be generated
	 */
	public void executeTestSuites()
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
				testSuite = testSuiteMap.get(name);

				if(testSuite == null)
				{
					throw new InvalidConfigurationException("Invalid test suite name '{}' specified in limited test suites", name);
				}

				executeTestSuite(testSuite);
			}
		}
		// if no limited test suites are specified execute all test suites
		else
		{
			for(TestSuite testSuite : testSuiteMap.values())
			{
				executeTestSuite(testSuite);
			}
		}

		try
		{
			Template freemarkerTemplate = new Template("report", 
					new InputStreamReader(AutomationLauncher.class.getResourceAsStream("/report-template.html")), freemarkerConfiguration);

			FileWriter writer = new FileWriter(new File(reportFolder, "test-report.html"));
			freemarkerTemplate.process(fullExecutionDetails, writer);

			writer.flush();
			writer.close();
			
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(reportFolder, "test-results.json"), fullExecutionDetails);
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating test result report");
		}
		
		File resourcesFolder = new File(reportFolder, "resources");
		resourcesFolder.mkdir();
		
		try
		{
			FileUtils.copyDirectory(new File("." + File.separator + "report-resources"), resourcesFolder);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while copying resource files to report folder - {}", reportFolder.getPath());
		}
	}
}
