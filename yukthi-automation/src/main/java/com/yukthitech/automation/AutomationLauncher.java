package com.yukthitech.automation;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.common.AutomationUtils;
import com.yukthitech.automation.config.ApplicationConfiguration;
import com.yukthitech.automation.config.IConfiguration;
import com.yukthitech.automation.test.TestDataFile;
import com.yukthitech.automation.test.TestSuite;
import com.yukthitech.automation.test.TestSuiteExecutor;
import com.yukthitech.automation.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

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
	 * Loads test suites from the test suite folder specified by app
	 * configuration.
	 * 
	 * @param context current context
	 * @param appConfig
	 *            Application config to be used
	 * @return Test suites mapped by name.
	 */
	private static TestSuiteGroup loadTestSuites(AutomationContext context, ApplicationConfiguration appConfig)
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

		// load the test suites recursively
		TreeSet<File> xmlFiles = AutomationUtils.loadXmlFiles(testCaseFolder);
		
		TestDataFile testDataFile = new TestDataFile();
		FileInputStream fis = null;
		String filePath = null;
		
		TestSuiteGroup testSuiteGroup = new TestSuiteGroup();
		
		File setupFile = null, cleanupFile = null;
		
		for(File xmlFile : xmlFiles)
		{
			testDataFile = new TestDataFile();

			try
			{
				filePath = xmlFile.getPath();
				fis = new FileInputStream(xmlFile);

				logger.debug("Loading test data file: {}", filePath);
				
				XMLBeanParser.parse(fis, testDataFile, defaultParserHandler);
				fis.close();
				
				for(TestSuite testSuite : testDataFile.getTestSuites())
				{
					logger.debug("Loading test suite '{}' from file: {}", testSuite.getName(), filePath);
					testSuiteGroup.addTestSuite(testSuite);
				}
				
				if(testDataFile.getSetup() != null)
				{
					if(setupFile != null)
					{
						throw new InvalidStateException("Duplicate global setups specified. Files: [{}, {}]", setupFile.getPath(), xmlFile.getPath());
					}
					
					testSuiteGroup.setSetup(testDataFile.getSetup());
					setupFile = xmlFile;
				}
				
				if(testDataFile.getCleanup() != null)
				{
					if(cleanupFile != null)
					{
						throw new InvalidStateException("Duplicate global cleaups specified. Files: [{}, {}]", cleanupFile.getPath(), xmlFile.getPath());
					}
					
					testSuiteGroup.setCleanup(testDataFile.getCleanup());
					cleanupFile = xmlFile;
				}
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while loading test suite from file: {}", xmlFile.getPath());
			}
		}
		
		// validate test suites has valid dependencies
		for(TestSuite testSuite : testSuiteGroup.getTestSuites())
		{
			if(testSuite.getDependencies() == null)
			{
				continue;
			}

			for(String dependency : testSuite.getDependencies())
			{
				if(!testSuiteGroup.isValidTestSuiteName(dependency))
				{
					throw new InvalidConfigurationException("Invalid dependency '{}' specified for test suite - {}", dependency, testSuite.getName());
				}
			}
		}
		
		logger.debug("Found required configurations by this context to be: {}", context.getConfigurations());
		
		return testSuiteGroup;
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
		Collection<IConfiguration<Object>> configurations = (Collection) context.getConfigurations();
		
		for(IConfiguration<Object> config : configurations)
		{
			logger.debug("Initializing configuration bean: {}", config.getClass().getName());
			
			args = argBeans.get(config.getArgumentBeanType());
			config.initialize(context, args);
		}
	}

	/**
	 * Loads application configuration from sepcified file.
	 * 
	 * @param appConfigurationFile
	 *            Application config file to load.
	 * @return Loaded application config.
	 */
	private static ApplicationConfiguration loadApplicationConfiguration(File appConfigurationFile) throws Exception
	{
		FileInputStream fis = new FileInputStream(appConfigurationFile);
		
		ApplicationConfiguration appConfig = new ApplicationConfiguration();
		XMLBeanParser.parse(fis, appConfig);
		
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
		System.out.println("Executing main function of aautomation layunche...");
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
		ApplicationConfiguration appConfig = loadApplicationConfiguration(appConfigurationFile);
		AutomationContext context = new AutomationContext(appConfig);
		
		// load test suites
		TestSuiteGroup testSuiteGroup = loadTestSuites(context, appConfig);

		//initialize the configurations
		String extendedCommandLineArgs[] = {};
		
		if(args.length > 2)
		{
			extendedCommandLineArgs = Arrays.copyOfRange(args, 2, args.length);
		}
	
		logger.debug("Found extended arguments to be: {}", Arrays.toString(extendedCommandLineArgs));
		initalizeConfigurations(context, extendedCommandLineArgs);
		
		//execute test suites
		TestSuiteExecutor testSuiteExecutor = new TestSuiteExecutor(context, testSuiteGroup, reportFolder);
		testSuiteExecutor.executeTestSuites();
	}
}