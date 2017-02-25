package com.yukthitech.automation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.common.AutomationUtils;
import com.yukthitech.automation.config.IApplicationConfiguration;
import com.yukthitech.automation.config.IConfiguration;
import com.yukthitech.automation.test.FullExecutionDetails;
import com.yukthitech.automation.test.TestCase;
import com.yukthitech.automation.test.TestCaseResult;
import com.yukthitech.automation.test.TestStatus;
import com.yukthitech.automation.test.TestSuite;
import com.yukthitech.automation.test.TestSuiteStatus;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Main class which executes the test suites of tha application.
 * 
 * @author akiran
 */
public class AutomationLauncher
{
	private static Logger logger = LogManager.getLogger(AutomationLauncher.class);
	
	private static final String COMMAND_SYNTAX = String.format("java %s <app-config-file> <report-folder> extended-args...", AutomationLauncher.class.getName());

	/**
	 * Free marker configuraton.
	 */
	private static Configuration freemarkerConfiguration = new Configuration();

	/**
	 * The log.
	 **/
	private static String LOG = ".log";

	/**
	 * Loads test suites from the test suite folder specified by app
	 * configuration.
	 * 
	 * @param context current context
	 * @param appConfig
	 *            Application config to be used
	 * @return Test suites mapped by name.
	 */
	private static Map<String, TestSuite> loadTestSuites(AutomationContext context, IApplicationConfiguration appConfig)
	{
		AutomationReserveNodeHandler reserveNodeHandler = new AutomationReserveNodeHandler(context, appConfig);
		
		DefaultParserHandler defaultParserHandler = new DefaultParserHandler();
		defaultParserHandler.registerReserveNodeHandler(reserveNodeHandler);
		
		logger.debug("Loading test suites from folder - {}", appConfig.getTestSuiteFolder());

		File testCaseFolder = new File(appConfig.getTestSuiteFolder());

		if(!testCaseFolder.exists() && !testCaseFolder.isDirectory())
		{
			System.err.println("Invalid test suite folder specified - " + testCaseFolder);
			System.exit(-1);
		}

		Map<String, TestSuite> testSuites = new HashMap<>();

		// load the test suites recursively
		TreeSet<File> xmlFiles = AutomationUtils.loadXmlFiles(testCaseFolder);
		
		for(File xmlFile : xmlFiles)
		{
			TestSuite testSuite = new TestSuite();

			try
			{
				FileInputStream fis = new FileInputStream(xmlFile);
				XMLBeanParser.parse(fis, testSuite, defaultParserHandler);
				testSuites.put(testSuite.getName(), testSuite);
				fis.close();
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while loading test suite from file: {}", xmlFile.getPath());
			}
		}
		
		// validate test suites has valid dependencies
		for(TestSuite testSuite : testSuites.values())
		{
			if(testSuite.getDependencies() == null)
			{
				continue;
			}

			for(String dependency : testSuite.getDependencies())
			{
				if(!testSuites.containsKey(dependency))
				{
					throw new InvalidConfigurationException("Invalid dependency '{}' specified for test suite - {}", dependency, testSuite.getName());
				}
			}
		}
		
		logger.debug("Found required configurations by this context to be: {}", context.getConfigurations());
		
		return testSuites;
	}
	
	/**
	 * Initializes the configurations required by current context.
	 * @param context context whose configurations needs to be initialized
	 * @param extendedCommandLineArgs Extended command line arguments
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void initalizeConfigurations(AutomationContext context, String extendedCommandLineArgs[])
	{
		//fetch the argument configuration types required
		List<Class<?>> argBeanTypes = context.getConfigurations().stream()
				.map(config -> config.getArgumentBeanType())
				.filter(type -> (type != null))
				.collect(Collectors.toList());
		
		argBeanTypes = new ArrayList<>(argBeanTypes);
		argBeanTypes.add(BasicArguments.class);

		//if any type is required creation command line options and parse command line arguments
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(argBeanTypes.toArray(new Class<?>[0]));
		Map<Class<?>, Object> argBeans = null;
		
		try
		{
			argBeans = commandLineOptions.parseBeans(extendedCommandLineArgs);
		} catch(MissingArgumentException e)
		{
			System.err.println(e.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			System.exit(-1);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			System.exit(-1);
		}
		
		context.setBasicArguments( (BasicArguments) argBeans.get(BasicArguments.class) );
		
		//initialize the configurations
		Object args = null;
		List<IConfiguration<Object>> configurations = (List) context.getConfigurations();
		
		for(IConfiguration<Object> config : configurations)
		{
			logger.debug("Initializing configuration bean: {}", config.getClass().getName());
			
			args = argBeans.get(config);
			config.initialize(context, args);
		}
	}

	/**
	 * Executes specified test suite and its dependencies recursively.
	 * 
	 * @param context
	 *            Automation context
	 * @param testSuite
	 *            Test suite to execute
	 * @param fullExecutionDetails
	 *            Execution details to track
	 * @param logsFolder
	 *            Logs folder
	 * @param testSuiteMap
	 *            Test suite map to get dependency test suites
	 * @return True if all dependencies and test cases passed.
	 */
	private static boolean executeTestSuite(AutomationContext context, TestSuite testSuite, FullExecutionDetails fullExecutionDetails, File logsFolder, Map<String, TestSuite> testSuiteMap)
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

				executeTestSuite(context, depTestSuite, fullExecutionDetails, logsFolder, testSuiteMap);
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

		for(TestCase testCase : testSuite.getTestCases())
		{
			if(restrictedTestCases != null && !restrictedTestCases.contains(testCase.getName()))
			{
				continue;
			}
			
			logger.debug("Executing test case '{}' in test suite - {}", testCase.getName(), testSuite.getName());

			testFileName = testSuite.getName() + "_" + testCase.getName();

			testCaseResult = testCase.execute(context);

			if(testCaseResult.getStatus() != TestStatus.SUCCESSUFUL)
			{
				successful = false;
			}

			try
			{
				FileUtils.writeStringToFile(new File(logsFolder, testFileName + LOG), testCaseResult.getExecutionLog());
				testCaseResult.setExecutionLog("");
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while creating test log file - {}", new File(logsFolder, testFileName + LOG));
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
	private static void executeTestSuites(AutomationContext context, Map<String, TestSuite> testSuiteMap, File reportFolder)
	{
		// create logs folder
		File logsFolder = new File(reportFolder, "logs");
		logsFolder.mkdirs();

		FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
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

				executeTestSuite(context, testSuite, fullExecutionDetails, logsFolder, testSuiteMap);
			}
		}
		// if no limited test suites are specified execute all test suites
		else
		{
			for(TestSuite testSuite : testSuiteMap.values())
			{
				executeTestSuite(context, testSuite, fullExecutionDetails, logsFolder, testSuiteMap);
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
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while generating test result report");
		}
	}

	/**
	 * Loads application configuration from sepcified file.
	 * 
	 * @param appConfigurationFile
	 *            Application config file to load.
	 * @return Loaded application config.
	 */
	private static IApplicationConfiguration loadApplicationConfiguration(File appConfigurationFile) throws Exception
	{
		FileInputStream fis = new FileInputStream(appConfigurationFile);
		IApplicationConfiguration appConfig = (IApplicationConfiguration) XMLBeanParser.parse(fis);
		fis.close();

		return appConfig;
	}

	/**
	 * Automation entry point.
	 * 
	 * @param args
	 *            CMD line arguments.
	 */
	public static void main(String[] args) throws Exception
	{
		File currentFolder = new File(".");

		logger.debug("Executing from folder: " + currentFolder.getCanonicalPath());

		if(args.length < 2)
		{
			System.err.println("Invalid number of arguments specified");
			System.err.println("Syntax: " + COMMAND_SYNTAX);
			System.exit(-1);
		}

		File appConfigurationFile = new File(args[0]);
		File reportFolder = new File(args[1]);

		if(!appConfigurationFile.exists())
		{
			System.err.println("Invalid application configuration file - " + appConfigurationFile);
			System.exit(-1);
		}

		// force delete report folder, if any
		if(reportFolder.exists())
		{
			FileUtils.forceDelete(reportFolder);
			reportFolder.mkdirs();
		}

		// load the configuration file
		IApplicationConfiguration appConfig = loadApplicationConfiguration(appConfigurationFile);
		AutomationContext context = new AutomationContext(appConfig);
		
		// load test suites
		Map<String, TestSuite> testSuites = loadTestSuites(context, appConfig);

		//initialize the configurations
		String extendedCommandLineArgs[] = {};
		
		if(args.length > 2)
		{
			extendedCommandLineArgs = Arrays.copyOfRange(args, 2, args.length);
		}
		
		initalizeConfigurations(context, extendedCommandLineArgs);
		
		//execute test suites
		executeTestSuites(context, testSuites, reportFolder);
	}
}