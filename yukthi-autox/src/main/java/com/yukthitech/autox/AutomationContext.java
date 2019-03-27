package com.yukthitech.autox;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.event.DummyAutomationListener;
import com.yukthitech.autox.event.IAutomationListener;
import com.yukthitech.autox.logmon.ILogMonitor;
import com.yukthitech.autox.logmon.LogFile;
import com.yukthitech.autox.monitor.MonitorServer;
import com.yukthitech.autox.monitor.ienv.ContextAttributeDetails;
import com.yukthitech.autox.monitor.ienv.InteractiveServerReady;
import com.yukthitech.autox.storage.PersistenceStorage;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.utils.cli.CommandLineOptions;
import com.yukthitech.utils.cli.MissingArgumentException;
import com.yukthitech.utils.cli.OptionsFactory;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Automation Context information. 
 * @author akiran
 */
public class AutomationContext
{
	private static Logger logger = LogManager.getLogger(AutomationContext.class);
	
	/**
	 * Automation context that can be accessed anywhere needed.
	 */
	private static AutomationContext instance;
	
	/**
	 * Basic arguments specified from command line context.
	 */
	private BasicArguments basicArguments;
	
	/**
	 * Map to hold context attributes.
	 */
	private Map<String, Object> nameToAttr = new HashMap<String, Object>();
	
	/**
	 * Application configuration.
	 */
	private ApplicationConfiguration appConfiguration;
	
	/**
	 * Maintains list of required plugins required by loaded test suites.
	 */
	private Map<Class<?>, IPlugin<?>> requiredPlugins = new HashMap<>();
	
	/**
	 * Maintain list of plugins which are already initialized.
	 */
	private Set<Class<?>> initializedPlugins = new HashSet<>();
	
	/**
	 * List of configured log monitors.
	 */
	private Map<String, ILogMonitor> logMonitors;
	
	/**
	 * Work directory to be used for this context.
	 */
	private File workDirectory;
	
	/**
	 * Name to step group mapping.
	 */
	private Map<String, Function> nameToFunction = new HashMap<>();
	
	/**
	 * Listener for automation.
	 */
	private IAutomationListener automationListener = new DummyAutomationListener();
	
	/**
	 * List of summary messages.
	 */
	private List<String> summaryMessages = new ArrayList<String>();
	
	/**
	 * Persistence storage to persist data across the executions.
	 */
	private PersistenceStorage persistenceStorage;
	
	/**
	 * Current test suite being executed.
	 */
	private TestSuite activeTestSuite;
	
	/**
	 * Current test case being executed.
	 */
	private TestCase activeTestCase;
	
	/**
	 * Current test case data for which test case being executed, if any.
	 */
	private TestCaseData activeTestCaseData;
	
	/**
	 * Maintains list of extended command line argument which will be used
	 * during plugin initialization.
	 */
	private String extendedCommandLineArgs[];
	
	/**
	 * Report folder where reports should be generated.
	 */
	private File reportFolder;
	
	/** The internal context att. */
	private Map<String, Object> internalContextAtt = new HashMap<>();
	
	/**
	 * Current execution logger. Can be null.
	 */
	private ExecutionLogger executionLogger;
	
	/**
	 * Used to send monitor messages to connected client.
	 */
	private MonitorServer monitorServer;
	
	/**
	 * Flag indicating if setup execution is currently going on.
	 */
	private boolean setupExecution;
	
	/**
	 * Flag indicating if cleanup execution is currently going on.
	 */
	private boolean cleanupExecution;
	
	/**
	 * Flag indicating if this context ready for client interaction.
	 */
	private boolean readyToInteract;
	
	/**
	 * The test suite parser handler.
	 */
	private TestSuiteParserHandler testSuiteParserHandler;
	
	/**
	 * The interactive environment context.
	 */
	private InteractiveEnvironmentContext interactiveEnvironmentContext;
	
	/**
	 * Used to maintain parameter stack.
	 */
	private Stack<Map<String, Object>> parametersStack = new Stack<>();
	
	/**
	 * Manages the stack trace of execution.
	 */
	private ExecutionStack executionStack = new ExecutionStack();
	
	/**
	 * Constructor.
	 * @param appConfiguration Application configuration
	 */
	public AutomationContext(ApplicationConfiguration appConfiguration)
	{
		this.appConfiguration = appConfiguration;
		
		List<ILogMonitor> logMontLst = appConfiguration.getLogMonitors();
		
		if(logMontLst != null && !logMontLst.isEmpty())
		{
			logMonitors = logMontLst.stream().collect(Collectors.toMap(monit -> monit.getName() , monit -> monit));
		}
		
		this.workDirectory = new File(appConfiguration.getWorkDirectory());
		
		try
		{
			if(workDirectory.exists())
			{
				AutomationUtils.deleteFolder(workDirectory);
			}
			
			FileUtils.forceMkdir(workDirectory);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while cleaning up work directory", ex);
		}
		
		AutomationContext.instance = this;
		
		this.persistenceStorage = new PersistenceStorage(appConfiguration);
	}
	
	/**
	 * Sets the used to send monitor messages to connected client.
	 *
	 * @param monitorServer the new used to send monitor messages to connected client
	 */
	public void setMonitorServer(MonitorServer monitorServer)
	{
		this.monitorServer = monitorServer;
	}
	
	/**
	 * Gets the automation context that can be accessed anywhere needed.
	 *
	 * @return the automation context that can be accessed anywhere needed
	 */
	public static AutomationContext getInstance()
	{
		return instance;
	}
	
	/**
	 * Sets the maintains list of extended command line argument which will be used during plugin initialization.
	 *
	 * @param extendedCommandLineArgs the new maintains list of extended command line argument which will be used during plugin initialization
	 */
	public void setExtendedCommandLineArgs(String[] extendedCommandLineArgs)
	{
		this.extendedCommandLineArgs = extendedCommandLineArgs;
	}
	
	/**
	 * Sets the current test suite being executed.
	 *
	 * @param currentTestSuite the new current test suite being executed
	 */
	public void setActiveTestSuite(TestSuite currentTestSuite)
	{
		this.activeTestSuite = currentTestSuite;
	}
	
	/**
	 * Gets the current test suite being executed.
	 *
	 * @return the current test suite being executed
	 */
	public TestSuite getActiveTestSuite()
	{
		return activeTestSuite;
	}
	
	/**
	 * Clears active test suite name.
	 */
	public void clearActiveTestSuite()
	{
		this.activeTestSuite = null;
	}
	
	/**
	 * Tests active test case and data for which execution is going on.
	 * @param testCase active test case
	 * @param testCaseData active test case data, if any
	 */
	public void setActiveTestCase(TestCase testCase, TestCaseData testCaseData)
	{
		this.activeTestCase = testCase;
		this.activeTestCaseData = testCaseData;
	}
	
	/**
	 * Clear active test case.
	 */
	public void clearActiveTestCase()
	{
		this.activeTestCase = null;
		this.activeTestCaseData = null;
	}
	
	/**
	 * Gets the flag indicating if setup execution is currently going on.
	 *
	 * @return the flag indicating if setup execution is currently going on
	 */
	public boolean isSetupExecution()
	{
		return setupExecution;
	}

	/**
	 * Sets the flag indicating if setup execution is currently going on.
	 *
	 * @param setupExecution the new flag indicating if setup execution is currently going on
	 */
	public void setSetupExecution(boolean setupExecution)
	{
		this.setupExecution = setupExecution;
	}

	/**
	 * Gets the flag indicating if cleanup execution is currently going on.
	 *
	 * @return the flag indicating if cleanup execution is currently going on
	 */
	public boolean isCleanupExecution()
	{
		return cleanupExecution;
	}

	/**
	 * Sets the flag indicating if cleanup execution is currently going on.
	 *
	 * @param cleanupExecution the new flag indicating if cleanup execution is currently going on
	 */
	public void setCleanupExecution(boolean cleanupExecution)
	{
		this.cleanupExecution = cleanupExecution;
	}

	/**
	 * Gets the current test case being executed.
	 *
	 * @return the current test case being executed
	 */
	public TestCase getActiveTestCase()
	{
		return activeTestCase;
	}
	
	/**
	 * Gets the current test case data for which test case being executed, if any.
	 *
	 * @return the current test case data for which test case being executed, if any
	 */
	public TestCaseData getActiveTestCaseData()
	{
		return activeTestCaseData;
	}
	
	/**
	 * Sets the basic arguments specified from command line context.
	 *
	 * @param basicArguments the new basic arguments specified from command line context
	 */
	public void setBasicArguments(BasicArguments basicArguments)
	{
		this.basicArguments = basicArguments;
	}
	
	/**
	 * Gets the basic arguments specified from command line context.
	 *
	 * @return the basic arguments specified from command line context
	 */
	public BasicArguments getBasicArguments()
	{
		return basicArguments;
	}
	
	/**
	 * Sets the specified attribute with specified value.
	 * @param name Name of the attribute
	 * @param value Value of the attribute
	 */
	public void setAttribute(String name, Object value)
	{
		nameToAttr.put(name, value);
		
		if(monitorServer != null)
		{
			monitorServer.sendAsync(new ContextAttributeDetails(name, value));
		}
	}
	
	/**
	 * Sets internal context attribute which will be accessible within the java code.
	 * @param name name of the attribute
	 * @param value value to set
	 */
	public void setInternalAttribute(String name, Object value)
	{
		internalContextAtt.put(name, value);
	}
	
	/**
	 * Clears all context attributes.
	 */
	public void clearAttributes()
	{
		nameToAttr.clear();
		internalContextAtt.clear();
	}
	
	/**
	 * Fetches the attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getAttribute(String name)
	{
		return nameToAttr.get(name);
	}
	
	/**
	 * Fetches internal attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getInternalAttribute(String name)
	{
		return internalContextAtt.get(name);
	}
	
	/**
	 * Removes attribute with specified name.
	 * @param name Name of the attribute to remove.
	 * @return Current attribute value.
	 */
	public Object removeAttribute(String name)
	{
		return nameToAttr.remove(name);
	}
	
	/**
	 * Removes internal attribute with specified name.
	 * @param name Name of internal attribute to remove.
	 * @return value of internal attribute being removed
	 */
	public Object removeInternalAttribute(String name)
	{
		return internalContextAtt.get(name);
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttributeMap()
	{
		return Collections.unmodifiableMap(nameToAttr);
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttr()
	{
		return Collections.unmodifiableMap(nameToAttr);
	}
	
	/**
	 * Used to push the parameters on stack of the function going to be executed on. Expected to be
	 * called only before fucntion execution by framework itself.
	 * @param parameters parameters to be pushed
	 */
	public void pushParameters(Map<String, Object> parameters)
	{
		if(parameters == null)
		{
			parameters = Collections.emptyMap();
		}
		
		parametersStack.push(parameters);
	}
	
	/**
	 * Pops the parameters from the function stack.
	 */
	public void popParameters()
	{
		parametersStack.pop();
	}
	
	public Map<String, Object> getParam()
	{
		if(parametersStack.isEmpty())
		{
			throw new InvalidStateException("Parameters are accessed outside the function");
		}
		
		return (Map<String, Object>) parametersStack.peek();
	}
	
	public Object getParameter(String name)
	{
		Map<String, Object> paramMap = (Map<String, Object>) getParam();
		
		if(paramMap == null)
		{
			return null;
		}
		
		return paramMap.get(name);
	}
	
	/**
	 * Adds required plugin to the context.
	 * @param plugin plugin to add
	 */
	void addRequirePlugin(IPlugin<?> plugin)
	{
		this.requiredPlugins.put(plugin.getClass(), plugin);
	}
	
	/**
	 * Fetches all required plugins required by this context.
	 * @return required plugins.
	 */
	public Collection<IPlugin<?>> getPlugins()
	{
		return Collections.unmodifiableCollection(requiredPlugins.values());
	}
	
	/**
	 * Initalize plugin.
	 *
	 * @param pluginType the plugin type
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initalizePlugin(Class<?> pluginType)
	{
		IPlugin<Object> plugin = (IPlugin) requiredPlugins.get(pluginType);
		
		if(plugin == null)
		{
			return;
		}
		
		Class<?> pluginArgType = plugin.getArgumentBeanType();
		
		if(pluginArgType == null || Object.class.equals(pluginArgType))
		{
			initializedPlugins.add(pluginType);
			return;
		}
		
		logger.debug("Initializing plugin: {}", plugin.getClass().getName());
		
		List<Class<?>> argBeanTypes = new ArrayList<>();
		argBeanTypes.add(pluginArgType);

		//if any type is required creation command line options and parse command line arguments
		CommandLineOptions commandLineOptions = OptionsFactory.buildCommandLineOptions(argBeanTypes.toArray(new Class<?>[0]));
		Map<Class<?>, Object> argBeans = null;
		
		try
		{
			argBeans = commandLineOptions.parseBeans(extendedCommandLineArgs);
		} catch(MissingArgumentException e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(-1);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			System.exit(-1);
		}

		Object args = argBeans.get(plugin.getArgumentBeanType());
		plugin.initialize(this, args);
		
		initializedPlugins.add(pluginType);
	}
	
	/**
	 * Fetches the plugin of specified plugin type.
	 * @param pluginType plugin type to fetch
	 * @return matching plugin
	 */
	@SuppressWarnings("unchecked")
	public <T extends IPlugin<?>> T getPlugin(Class<T> pluginType)
	{
		if(!initializedPlugins.contains(pluginType))
		{
			initalizePlugin(pluginType);
		}
		
		return (T) requiredPlugins.get(pluginType);
	}
	
	/**
	 * Gets the application configuration.
	 *
	 * @return the application configuration
	 */
	public ApplicationConfiguration getAppConfiguration()
	{
		return appConfiguration;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getAppProperties()
	{
		return (Map) appConfiguration.getApplicationProperties();
	}
	
	/**
	 * Starts all registered log monitors.
	 */
	public synchronized void startLogMonitoring()
	{
		if(logMonitors == null)
		{
			return;
		}
		
		for(ILogMonitor monitor : logMonitors.values())
		{
			monitor.startMonitoring();
		}
	}
	
	/**
	 * Stops the log monitors and collects the log files generated.
	 * @return Collected log files
	 */
	public synchronized Map<String, File> stopLogMonitoring()
	{
		if(logMonitors == null)
		{
			return null;
		}
		
		Map<String, File> logFiles = new HashMap<>();
		
		for(Map.Entry<String, ILogMonitor> entry : this.logMonitors.entrySet())
		{
			List<LogFile> monitorLogFiles = entry.getValue().stopMonitoring();
			
			if(monitorLogFiles == null)
			{
				continue;
			}
			
			for(LogFile logFile : monitorLogFiles)
			{
				String key = entry.getKey();
				
				if(!key.equals(logFile.getName()))
				{
					key = key + "[" + logFile.getName() + "]";
				}
				
				logFiles.put(key, logFile.getFile());
			}
		}
		
		return logFiles;
	}
	
	/**
	 * Fetches the data bean map.
	 * @return data bean map.
	 */
	public Map<String, Object> getDataBeans()
	{
		return appConfiguration.getDataBeans();
	}
	
	/**
	 * Ease method to access data beans.
	 * @return
	 */
	public Map<String, Object> getData()
	{
		return appConfiguration.getDataBeans();
	}

	/**
	 * Gets the work directory to be used for this context.
	 *
	 * @return the work directory to be used for this context
	 */
	public File getWorkDirectory()
	{
		return workDirectory;
	}
	
	/**
	 * Adds specified test group.
	 * @param function group to add.
	 */
	public void addFunction(Function function)
	{
		if(StringUtils.isEmpty(function.getName()))
		{
			throw new InvalidArgumentException("Function can not be added without name");
		}
		
		if(nameToFunction.containsKey(function.getName()))
		{
			throw new InvalidStateException("Duplicate function name encountered: {}", function.getName());
		}
		
		function.markAsFunctionGroup();
		nameToFunction.put(function.getName(), function);
	}
	
	/**
	 * Clears step groups.
	 */
	public void clearFunctions()
	{
		nameToFunction.clear();
	}
	
	/**
	 * Adds the specified step groups.
	 *
	 * @param functions step groups to add
	 */
	public void addFunctions(Map<String, Function> functions)
	{
		nameToFunction.putAll(functions);
	}
	
	/**
	 * Fetches the step group with specified name.
	 * @param name name of step group.
	 * @return matching group
	 */
	public Function getFunction(String name)
	{
		//check if current test suite has the group, if present give that higher preference
		if(activeTestSuite != null)
		{
			Function function = activeTestSuite.getFunction(name);
			
			if(function != null)
			{
				return function;
			}
		}
		
		return nameToFunction.get(name);
	}

	/**
	 * Gets the listener for automation.
	 *
	 * @return the listener for automation
	 */
	public IAutomationListener getAutomationListener()
	{
		return automationListener;
	}

	/**
	 * Sets the listener for automation.
	 *
	 * @param automationListener the new listener for automation
	 */
	public void setAutomationListener(IAutomationListener automationListener)
	{
		if(automationListener == null)
		{
			throw new NullPointerException("Automation listener can not be null.");
		}
		
		this.automationListener = automationListener;
	}
	
	/**
	 * Adds specified summary message to this context.
	 * @param mssg message to be added
	 */
	public void addSumarryMessage(String mssg)
	{
		this.summaryMessages.add(mssg);
	}
	
	/**
	 * Gets the list of summary messages.
	 *
	 * @return the list of summary messages
	 */
	public List<String> getSummaryMessages()
	{
		return summaryMessages;
	}
	
	/**
	 * Gets the persistence storage to persist data across the executions.
	 *
	 * @return the persistence storage to persist data across the executions
	 */
	public PersistenceStorage getPersistenceStorage()
	{
		return persistenceStorage;
	}

	/**
	 * Gets the report folder where reports should be generated.
	 *
	 * @return the report folder where reports should be generated
	 */
	public File getReportFolder()
	{
		return reportFolder;
	}

	/**
	 * Sets the report folder where reports should be generated.
	 *
	 * @param reportFolder the new report folder where reports should be generated
	 */
	public void setReportFolder(File reportFolder)
	{
		this.reportFolder = reportFolder;
	}
	
	/**
	 * Gets the current execution logger. Can be null.
	 *
	 * @return the current execution logger
	 */
	public ExecutionLogger getExecutionLogger()
	{
		return executionLogger;
	}

	/**
	 * Sets the current execution logger. Can be null.
	 *
	 * @param executionLogger the new current execution logger
	 */
	public void setExecutionLogger(ExecutionLogger executionLogger)
	{
		this.executionLogger = executionLogger;
	}
	
	/**
	 * Sends async monitor message to the connected client if any.
	 * @param mssg message to be sent.
	 */
	public void sendAsyncMonitorMessage(Serializable mssg)
	{
		if(monitorServer != null)
		{
			monitorServer.sendAsync(mssg);
		}
	}
	
	/**
	 * Send ready to interact.
	 */
	public void sendReadyToInteract()
	{
		readyToInteract = true;
		sendAsyncMonitorMessage(new InteractiveServerReady());
	}
	
	/**
	 * Gets the flag indicating if this context ready for client interaction.
	 *
	 * @return the flag indicating if this context ready for client interaction
	 */
	public boolean isReadyToInteract()
	{
		return readyToInteract;
	}
	
	/**
	 * Checks if is monitoring enabled.
	 *
	 * @return true, if is monitoring enabled
	 */
	public boolean isMonitoringEnabled()
	{
		return (monitorServer != null);
	}
	
	/**
	 * Gets the test suite parser handler.
	 *
	 * @return the test suite parser handler
	 */
	public TestSuiteParserHandler getTestSuiteParserHandler()
	{
		return testSuiteParserHandler;
	}

	/**
	 * Sets the test suite parser handler.
	 *
	 * @param testSuiteParserHandler the new test suite parser handler
	 */
	public void setTestSuiteParserHandler(TestSuiteParserHandler testSuiteParserHandler)
	{
		this.testSuiteParserHandler = testSuiteParserHandler;
	}

	/**
	 * Gets the interactive environment context.
	 *
	 * @return the interactive environment context
	 */
	public InteractiveEnvironmentContext getInteractiveEnvironmentContext()
	{
		return interactiveEnvironmentContext;
	}

	/**
	 * Sets the interactive environment context.
	 *
	 * @param interactiveEnvironmentContext the new interactive environment context
	 */
	public void setInteractiveEnvironmentContext(InteractiveEnvironmentContext interactiveEnvironmentContext)
	{
		this.interactiveEnvironmentContext = interactiveEnvironmentContext;
	}
	
	/**
	 * Gets the manages the stack trace of execution.
	 *
	 * @return the manages the stack trace of execution
	 */
	public ExecutionStack getExecutionStack()
	{
		return executionStack;
	}

	/**
	 * Closes all the plugins initialized in this context. Generally this method
	 * should be called before destroying this context.
	 */
	public void close()
	{
		for(IPlugin<?> plugin : this.requiredPlugins.values())
		{
			if(initializedPlugins.contains(plugin))
			{
				logger.debug("Closing plugin: " + plugin.getClass().getName());
				plugin.close();
			}
		}
	}
}