package com.yukthitech.autox.context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.TestSuiteParserHandler;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.config.IPluginSession;
import com.yukthitech.autox.debug.server.DebugServer;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.storage.PersistenceStorage;
import com.yukthitech.autox.test.CustomUiLocator;
import com.yukthitech.autox.test.Function;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Automation Context information. 
 * @author akiran
 */
public class AutomationContext
{
	/**
	 * Automation context that can be accessed anywhere needed.
	 */
	private static AutomationContext instance;
	
	/**
	 * Basic arguments specified from command line context.
	 */
	private BasicArguments basicArguments;
	
	/**
	 * Application configuration.
	 */
	private ApplicationConfiguration appConfiguration;
	
	/**
	 * Work directory to be used for this context.
	 */
	private File workDirectory;
	
	/**
	 * Name to step group mapping.
	 */
	private Map<String, Function> nameToFunction = new HashMap<>();
	
	/**
	 * Persistence storage to persist data across the executions.
	 */
	private PersistenceStorage persistenceStorage;
	
	/**
	 * Maintains list of extended command line argument which will be used
	 * during plugin initialization.
	 */
	private String extendedCommandLineArgs[];
	
	/**
	 * Report folder where reports should be generated.
	 */
	private File reportFolder;
	
	/**
	 * Used to send monitor messages to connected client.
	 */
	private DebugServer debugServer;
	
	/**
	 * The test suite parser handler.
	 */
	private TestSuiteParserHandler testSuiteParserHandler;
	
	/**
	 * Custom ui locators.
	 */
	private Map<String, CustomUiLocator> customUiLocators = new HashMap<>();
	
	/**
	 * Flag indicating if this context ready for client interaction.
	 */
	private boolean readyToInteract;

	/**
	 * Constructor.
	 * @param appConfiguration Application configuration
	 */
	public AutomationContext(ApplicationConfiguration appConfiguration)
	{
		this.appConfiguration = appConfiguration;
		
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
	public void setDebugServer(DebugServer monitorServer)
	{
		this.debugServer = monitorServer;
	}
	
	public DebugServer getDebugServer()
	{
		return debugServer;
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
	
	public String[] getExtendedCommandLineArgs()
	{
		return extendedCommandLineArgs;
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
		ExecutionContextManager.getExecutionContext().setAttribute(name, value);
	}
	
	public void setGlobalAttribute(String name, Object value)
	{
		ExecutionContextManager.getInstance().setGlobalAttribute(name, value);
	}
	
	/**
	 * Sets internal context attribute which will be accessible within the java code.
	 * @param name name of the attribute
	 * @param value value to set
	 */
	public void setInternalAttribute(String name, Object value)
	{
		ExecutionContextManager.getExecutionContext().setInternalAttribute(name, value);
	}
	
	/**
	 * Fetches the attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getAttribute(String name)
	{
		return ExecutionContextManager.getExecutionContext().getAttribute(name);
	}
	
	/**
	 * Fetches internal attribute value with specified name.
	 * @param name Name of attribute to fetch
	 * @return Attribute value
	 */
	public Object getInternalAttribute(String name)
	{
		return ExecutionContextManager.getExecutionContext().getInternalAttribute(name);
	}
	
	/**
	 * Removes attribute with specified name.
	 * @param name Name of the attribute to remove.
	 * @return Current attribute value.
	 */
	public Object removeAttribute(String name)
	{
		return ExecutionContextManager.getExecutionContext().removeAttribute(name);
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttributeMap()
	{
		return ExecutionContextManager.getExecutionContext().getAttr();
	}
	
	/**
	 * Fetches the attributes on the context as map.
	 * @return Context attributes.
	 */
	public Map<String, Object> getAttr()
	{
		return ExecutionContextManager.getExecutionContext().getAttr();
	}
	
	/**
	 * Used to push the parameters on stack of the function going to be executed on. Expected to be
	 * called only before fucntion execution by framework itself.
	 * @param parameters parameters to be pushed
	 */
	public void pushParameters(Map<String, Object> parameters)
	{
		ExecutionContextManager.getInstance().getExecutionContextStack().pushParameters(parameters);
	}
	
	/**
	 * Pops the parameters from the function stack.
	 */
	public void popParameters()
	{
		ExecutionContextManager.getInstance().getExecutionContextStack().popParameters();
	}
	
	public boolean isParamPresent()
	{
		return ExecutionContextManager.getInstance().getExecutionContextStack().isParamPresent();
	}
	
	public Map<String, Object> getParam()
	{
		return ExecutionContextManager.getInstance().getExecutionContextStack().getParam();
	}
	
	public Object getParameter(String name)
	{
		return ExecutionContextManager.getInstance().getExecutionContextStack().getParameter(name);
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
	
	/**
	 * For ease of access of app properties using context.
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getProp()
	{
		return (Map) appConfiguration.getApplicationProperties();
	}
	
	public ReportLogFile newLogFile(String name, String extension)
	{
		if(!extension.startsWith("."))
		{
			extension = "." + extension;
		}
		
		if(name.toLowerCase().endsWith(extension.toLowerCase()))
		{
			name = name.substring(0, name.length() - extension.length());
		}
		
		File logFolder = new File(reportFolder, IAutomationConstants.LOGS_FOLDER_NAME);
		String namePrefix = name + "_" + Long.toHexString(System.currentTimeMillis()).toLowerCase();
		File file = new File(logFolder, name + extension);
		int index = 1;
		
		while(file.exists())
		{
			file = new File(logFolder, namePrefix + "_" + index + extension);
			index++;
		}
		
		return new ReportLogFile(file);
	}
	
	public <P, S extends IPluginSession> S newPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		return ExecutionContextManager.getInstance().getPluginSession(pluginType);
	}

	/**
	 * Starts all registered log monitors.
	 */
	public void startLogMonitoring()
	{
		ExecutionContextManager.getExecutionContext().startLogMonitoring();
	}
	
	/**
	 * Stops the log monitors and collects the log files generated.
	 * @return Collected log files
	 */
	public Map<String, ReportLogFile> stopLogMonitoring(boolean flowErrored)
	{
		return ExecutionContextManager.getExecutionContext().stopMonitoring(flowErrored);
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
		Function function = ExecutionContextManager.getExecutionContext().getFunction(name);
		
		if(function != null)
		{
			return function;
		}
		
		return nameToFunction.get(name);
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
	public IExecutionLogger getExecutionLogger()
	{
		return ExecutionContextManager.getExecutionContext().getExecutionLogger();
	}

	/**
	 * Sets the current execution logger. Can be null.
	 *
	 * @param executionLogger the new current execution logger
	 */
	public void setExecutionLogger(IExecutionLogger executionLogger)
	{
		ExecutionContextManager.getExecutionContext().setExecutionLogger(executionLogger);
	}
	
	/**
	 * Send ready to interact.
	 */
	public void sendReadyToInteract()
	{
		readyToInteract = true;
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
		return (debugServer != null);
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

	public void addCustomUiLocator(CustomUiLocator locator)
	{
		if(customUiLocators.containsKey(locator.getName()))
		{
			throw new InvalidArgumentException("A custom ui locator with specified name already exist: %s", locator.getName());
		}
		
		this.customUiLocators.put(locator.getName(), locator);
	}
	
	public void addOrReplaceCustomUiLocator(CustomUiLocator locator)
	{
		this.customUiLocators.put(locator.getName(), locator);
	}

	public CustomUiLocator getCustomUiLocator(String name)
	{
		return this.customUiLocators.get(name);
	}
	
	public String getOverridableProp(String name)
	{
		String val = System.getProperty(name);
		
		if(val != null)
		{
			return val;
		}
		
		return appConfiguration.getApplicationProperties().getProperty(name);
	}
}