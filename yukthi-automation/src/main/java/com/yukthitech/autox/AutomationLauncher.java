package com.yukthitech.autox;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.AppConfigParserHandler;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteExecutor;
import com.yukthitech.autox.test.TestSuiteGroup;
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
	
	private static final String COMMAND_SYNTAX = String.format("java %s <app-config-file> extended-args...", AutomationLauncher.class.getName());

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
		DefaultParserHandler defaultParserHandler = new AutomationParserHandler(context, appConfig);
		
		logger.debug("Loading test suites from folders - {}", appConfig.getTestSuiteFolders());
		
		List<File> xmlFiles = new ArrayList<>();

		for(String folder : appConfig.getTestSuiteFolders())
		{
			File testCaseFolder = new File(folder);
	
			if(!testCaseFolder.exists() && !testCaseFolder.isDirectory())
			{
				System.err.println("Invalid test suite folder specified - " + testCaseFolder);
				System.exit(-1);
			}
	
			// load the test suites recursively
			xmlFiles.addAll( AutomationUtils.loadXmlFiles(testCaseFolder) );
		}
		
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
		
		logger.debug("Found required configurations by this context to be: {}", context.getPlugins());
		
		return testSuiteGroup;
	}
	
	/**
	 * Initializes the configurations required by current context.
	 * @param context context whose configurations needs to be initialized
	 * @param extendedCommandLineArgs Extended command line arguments
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void initalizePlugins(AutomationContext context, String extendedCommandLineArgs[])
	{
		//fetch the argument configuration types required
		List<Class<?>> argBeanTypes = context.getPlugins().stream()
				.map(config -> config.getArgumentBeanType())
				.filter(type -> (type != null))
				.collect(Collectors.toList());
		
		argBeanTypes = new ArrayList<>(argBeanTypes);
		
		//Add basic arguments type, so that on error its properties are not skipped in error message
		argBeanTypes.add(BasicArguments.class);

		//if any type is required creation command line options and parse command line arguments
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(argBeanTypes.toArray(new Class<?>[0]));
		Map<Class<?>, Object> argBeans = null;
		
		try
		{
			argBeans = commandLineOptions.parseBeans(extendedCommandLineArgs);
		} catch(MissingArgumentException e)
		{
			System.err.println("Error: " + e.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			System.exit(-1);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			System.exit(-1);
		}
		
		//initialize the configurations
		Object args = null;
		Collection<IPlugin<Object>> plugins = (Collection) context.getPlugins();
		
		for(IPlugin<Object> plugin : plugins)
		{
			logger.debug("Initializing plugin: {}", plugin.getClass().getName());
			
			args = argBeans.get(plugin.getArgumentBeanType());
			plugin.initialize(context, args);
		}
	}

	/**
	 * Loads application configuration from sepcified file.
	 * 
	 * @param appConfigurationFile
	 *            Application config file to load.
	 * @return Loaded application config.
	 */
	private static ApplicationConfiguration loadApplicationConfiguration(File appConfigurationFile, BasicArguments basicArguments) throws Exception
	{
		Properties appProperties = new Properties();
		
		if(basicArguments.getPropertiesFile() != null)
		{
			FileInputStream propInputStream = new FileInputStream(basicArguments.getPropertiesFile());
			appProperties.load(propInputStream);
		}
		
		FileInputStream fis = new FileInputStream(appConfigurationFile);
		
		ApplicationConfiguration appConfig = new ApplicationConfiguration(appProperties);
		XMLBeanParser.parse(fis, appConfig, new AppConfigParserHandler(appProperties));
		
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
		System.out.println("Executing main function of automation launcher...");
		File currentFolder = new File(".");
		
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(BasicArguments.class);

		logger.debug("Executing from folder: " + currentFolder.getCanonicalPath());

		if(args.length < 1)
		{
			System.err.println("Invalid number of arguments specified");
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}

		File appConfigurationFile = new File(args[0]);

		if(!appConfigurationFile.exists())
		{
			System.err.println("Invalid application configuration file - " + appConfigurationFile);
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}
		
		//initialize the configurations
		String extendedCommandLineArgs[] = {};
		
		if(args.length > 1)
		{
			extendedCommandLineArgs = Arrays.copyOfRange(args, 1, args.length);
		}

		BasicArguments basicArguments = null;
		
		try
		{
			basicArguments = (BasicArguments) commandLineOptions.parseBean(extendedCommandLineArgs);
		}catch(MissingArgumentException ex)
		{
			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}catch(Exception ex)
		{
			ex.printStackTrace();

			System.err.println("Error: " + ex.getMessage());
			System.err.println(commandLineOptions.fetchHelpInfo(COMMAND_SYNTAX));
			
			System.exit(-1);
		}

		File reportFolder = new File(basicArguments.getReportsFolder());

		// force delete report folder, if any
		if(reportFolder.exists())
		{
			FileUtils.forceDelete(reportFolder);
			reportFolder.mkdirs();
		}

		// load the configuration file
		ApplicationConfiguration appConfig = loadApplicationConfiguration(appConfigurationFile, basicArguments);
		AutomationContext context = new AutomationContext(appConfig);
		context.setBasicArguments(basicArguments);
		
		// load test suites
		TestSuiteGroup testSuiteGroup = loadTestSuites(context, appConfig);

		logger.debug("Found extended arguments to be: {}", Arrays.toString(extendedCommandLineArgs));
		initalizePlugins(context, extendedCommandLineArgs);
		
		//execute test suites
		TestSuiteExecutor testSuiteExecutor = new TestSuiteExecutor(context, testSuiteGroup, reportFolder);
		testSuiteExecutor.executeTestSuites();
		
		System.exit(0);
	}
}