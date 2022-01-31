package com.yukthitech.autox;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.event.IAutomationListener;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.autox.monitor.MonitorServer;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteExecutor;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;
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
	private static TestSuiteGroup loadTestSuites(AutomationContext context, ApplicationConfiguration appConfig, boolean loadTestSuites)
	{
		TestSuiteParserHandler defaultParserHandler = new TestSuiteParserHandler(context);
		context.setTestSuiteParserHandler(defaultParserHandler);
		
		if(!loadTestSuites)
		{
			return null;
		}
		
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
		
		TestDataFile testDataFile = null;
		FileInputStream fis = null;
		String filePath = null;
		
		TestSuiteGroup testSuiteGroup = new TestSuiteGroup();
		
		File setupFile = null, cleanupFile = null;
		List<File> limitFolders = context.getBasicArguments().getFolderLimitFiles();
		
		if(limitFolders != null)
		{
			logger.debug("Limiting the xml file loading to folders: {}", limitFolders);
		}

		Set<String> errors = new HashSet<>();

		for(File xmlFile : xmlFiles)
		{
			if(limitFolders != null)
			{
				boolean found = false;
				
				for(File folder : limitFolders)
				{
					if(AutomationUtils.isChild(folder, xmlFile))
					{
						found = true;
						break;
					}
				}
				
				if(!found)
				{
					logger.trace("Skiping file as it is not present in specified folder limits. File: {}", xmlFile.getPath());
					continue;
				}
			}
			
			testDataFile = new TestDataFile(context);
			defaultParserHandler.setFileBeingParsed(xmlFile.getName());

			try
			{
				filePath = xmlFile.getPath();
				fis = new FileInputStream(xmlFile);

				logger.debug("Loading test suite file: {}", filePath);
				
				XMLBeanParser.parse(fis, testDataFile, defaultParserHandler);
				fis.close();
				
				for(TestSuite testSuite : testDataFile.getTestSuites())
				{
					logger.debug("Loading test suite '{}' from file: {}", testSuite.getName(), filePath);
					
					testSuite.setFile(xmlFile);
					testSuiteGroup.addTestSuite(testSuite);
					
					if(testSuite.getTestCases() != null)
					{
						testSuite.getTestCases().forEach(tc -> tc.setFile(xmlFile));
					}
				}
				
				if(testDataFile.getSetup() != null)
				{
					if(setupFile != null)
					{
						throw new InvalidStateException("Duplicate global setups specified. Files: [{}, {}]", setupFile.getPath(), xmlFile.getPath());
					}
					
					if(testDataFile.getSetup().getLocation() == null)
					{
						testDataFile.getSetup().setLocation(filePath, -1);
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
					
					if(testDataFile.getCleanup().getLocation() == null)
					{
						testDataFile.getCleanup().setLocation(filePath, -1);
					}
					
					testSuiteGroup.setCleanup(testDataFile.getCleanup());
					cleanupFile = xmlFile;
				}
			} catch(Exception ex)
			{
				logger.error("An error occurred while loading file: %s", xmlFile.getPath(), ex);
				errors.add(String.format("Error File: %s\n\t\tError: %s\n\t\tCaused by: %s", xmlFile.getPath(), ex, ex.getCause()));
			}
		}
		
		if(!errors.isEmpty())
		{
			String errorStr = errors.stream().collect(Collectors.joining("\n\n\t"));
			throw new InvalidStateException("Failed to load test suite files. Following errors occurred: \n\t{}", errorStr);
		}

		logger.debug("Found required configurations by this context to be: {}", context.getPlugins());
		
		return testSuiteGroup;
	}
	
	/**
	 * Initializes the configurations required by current context.
	 * @param context context whose configurations needs to be initialized
	 * @param extendedCommandLineArgs Extended command line arguments
	 */
	private static void validateCommandLineArguments(AutomationContext context, String extendedCommandLineArgs[])
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
		
		try
		{
			commandLineOptions.parseBeans(extendedCommandLineArgs);
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
	}
	
	/**
	 * Fetches the monitoring port from system property if one is specified.
	 * @return
	 */
	private static Integer getMonitoringPort()
	{
		String portNumStr = System.getProperty(MonitorServer.SYS_PROP_MONITOR_PORT);
		
		if(portNumStr == null)
		{
			return null;
		}
		
		try
		{
			int port = Integer.parseInt(portNumStr);
			
			if(port <= 0)
			{
				throw new InvalidStateException("Invalid monitoring port is configured: {}", portNumStr);
			}
			
			return port;
		}catch(NumberFormatException ex)
		{
			throw new InvalidStateException("Invalid monitoring port is configured: {}", portNumStr);
		}
	}
	
	public static AutomationContext loadAutomationContext(File appConfigurationFile, String extendedCommandLineArgs[]) throws Exception
	{
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(BasicArguments.class);
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

		// force delete report folder, on error try for 5 times
		if(reportFolder.exists())
		{
			AutomationUtils.deleteFolder(reportFolder);
		}
		
		// load the configuration file
		ApplicationConfiguration appConfig = ApplicationConfiguration.loadApplicationConfiguration(appConfigurationFile, basicArguments);
		AutomationContext context = new AutomationContext(appConfig);
		
		ExpressionFactory.init(null, appConfig.getBasePackages());

		//set monitoring port info
		Integer monitorPort = getMonitoringPort();
		
		if(monitorPort != null)
		{
			MonitorServer server = MonitorServer.startManager(monitorPort);
			context.setMonitorServer(server);
		}
		
		context.setBasicArguments(basicArguments);
		context.setReportFolder(reportFolder);
		
		if(basicArguments.getAutomationListener() != null)
		{
			Class<?> listenerType = Class.forName(basicArguments.getAutomationListener());
			IAutomationListener listener = (IAutomationListener) listenerType.newInstance();
			
			context.setAutomationListener(listener);
		}
		
		return context;
	}
	
	public static TestSuiteGroup loadTestFiles(String args[]) throws Exception
	{
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
		
		//load automation context
		AutomationContext context = loadAutomationContext(appConfigurationFile, extendedCommandLineArgs);
		ApplicationConfiguration appConfig = context.getAppConfiguration();
		
		logger.debug("Found extended arguments to be: {}", Arrays.toString(extendedCommandLineArgs));
		validateCommandLineArguments(context, extendedCommandLineArgs);
		
		boolean loadTestSuites = true;
		boolean isInteractive = context.getBasicArguments().isInteractiveEnvironment();
		boolean isInteractiveExeGlobal = context.getBasicArguments().isInteractiveExecuteGlobal();
		
		if(isInteractive)
		{
			//test suites needs to be loaded only if global setup needs to be executed.
			loadTestSuites = isInteractiveExeGlobal;
		}
		
		// load test suites
		TestSuiteGroup testSuiteGroup = loadTestSuites(context, appConfig, loadTestSuites);
		
		return testSuiteGroup;
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
		
		try
		{
			// load test suites
			TestSuiteGroup testSuiteGroup = loadTestFiles(args);
			AutomationContext context = AutomationContext.getInstance();
			
			boolean isInteractive = context.getBasicArguments().isInteractiveEnvironment();
			boolean loadTestSuites = (testSuiteGroup != null);
	
			//execute test suites
			TestSuiteExecutor testSuiteExecutor = loadTestSuites ? new TestSuiteExecutor(context, testSuiteGroup) : null;
			
			if(isInteractive)
			{
				InteractiveEnvironmentContext interactiveEnvironmentContext = new InteractiveEnvironmentContext(testSuiteGroup);
				context.setInteractiveEnvironmentContext(interactiveEnvironmentContext);
				
				if(!context.isMonitoringEnabled())
				{
					System.err.println("Tried to start interactive environment without monitoring.");
					System.exit(-1);
				}
				
				boolean isInteractiveExeGlobal = context.getBasicArguments().isInteractiveExecuteGlobal();
				
				if(isInteractiveExeGlobal)
				{
					logger.debug("As part of interactive environment, executing global setup...");
					testSuiteExecutor.executeGlobalSetup();
				}
				
				logger.debug("Skipping actual test suite execution, as this is interactive environment exection.");
				context.sendReadyToInteract();
				return;
			}
			
			boolean res = testSuiteExecutor.executeTestSuites();
			
			context.close();
	
			if(!context.getBasicArguments().isReportOpeningDisalbed())
			{
				try
				{
					Desktop.getDesktop().open(new File(context.getReportFolder(), "index.html"));
				}catch(Exception ex)
				{
					logger.warn("Failed to open report html in browser. Ignoring the error: " + ex);
				}
			}
		
			System.exit( res ? 0 : -1 );
		}catch(Exception ex)
		{
			logger.error("An unhandled error occurred during execution", ex);
			System.exit(-1);
		}
	}
}