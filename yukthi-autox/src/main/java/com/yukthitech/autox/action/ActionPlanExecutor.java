package com.yukthitech.autox.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.AutomationReserveNodeHandler;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.TestSuiteParserHandler;
import com.yukthitech.autox.test.FullExecutionDetails;
import com.yukthitech.autox.test.ReportGenerator;
import com.yukthitech.autox.test.StepExecutor;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseResult;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Action plan executor.
 * @author akiran
 */
public class ActionPlanExecutor
{
	private static Logger logger = LogManager.getLogger(ActionPlanExecutor.class);
	
	/**
	 * Automation context used for step execution.
	 */
	private AutomationContext context;
	
	/**
	 * parser handler to parse steps.
	 */
	private TestSuiteParserHandler testSuiteParserHandler;
	
	/**
	 * Ide specific reserve node handling.
	 */
	private AutomationReserveNodeHandler reserveNodeHandler;
	
	/**
	 * Application configuration xml file.
	 */
	private String appConfigXmlFile;
	
	/**
	 * Path of application properties file.
	 */
	private String appPropertiesFile;
	
	/**
	 * Path where reports would be generated.
	 */
	private String outputFolderPath;
	
	/**
	 * Report generator for generating reports.
	 */
	private ReportGenerator reportGenerator = new ReportGenerator();

	/**
	 * Instantiates a new action plan executor.
	 *
	 * @param appConfigXmlFile the app config xml file
	 * @param appPropertiesFile the app properties file
	 */
	public ActionPlanExecutor(String appConfigXmlFile, String appPropertiesFile, String outputFolderPath)
	{
		this.appConfigXmlFile = appConfigXmlFile;
		this.appPropertiesFile = appPropertiesFile;
		this.outputFolderPath = outputFolderPath;
		
		init();
	}
	
	/**
	 * Initializes context.
	 */
	private void init()
	{
		try
		{
			String cmdArgs[] = new String[] { 
					"-rf", outputFolderPath, 
					"-prop", appPropertiesFile
			};
			
			logger.debug("Re-initializing ide engine..");
			context = AutomationLauncher.loadAutomationContext( new File(appConfigXmlFile), cmdArgs);
			
			reserveNodeHandler = new AutomationReserveNodeHandler(context, context.getAppConfiguration());
			testSuiteParserHandler = new TestSuiteParserHandler(context, reserveNodeHandler);
			
			testSuiteParserHandler.setFileBeingParsed("input");
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while initializing the context", ex);
		}
	}
	
	private String getName(String path)
	{
		int slashIdx = path.lastIndexOf("/");
		
		if(slashIdx >= 0)
		{
			path = path.substring(slashIdx + 1);
		}
		
		int dotIdx = path.lastIndexOf(".");
		
		if(dotIdx > 0)
		{
			path = path.substring(0, dotIdx);
		}
		
		return path;
	}
	
	/**
	 * Executes specified action plan xml file.
	 * @param actionFile
	 * @param initContextParams Initial context params to be populated on context.
	 * @throws IOException
	 */
	public boolean executeActionPlan(File actionFile, Map<String, Object> initContextParams) throws IOException
	{
		FileInputStream fis = new FileInputStream(actionFile);
		boolean res = executeActionPlan(getName(actionFile.getName()), fis, initContextParams);
		fis.close();
		
		return res;
	}

	/**
	 * Executes specified action plan resource file.
	 * @param resource action resource to execute
	 * @param initContextParams Initial context params to be populated on context.
	 * @throws IOException
	 */
	public boolean executeActionPlanResource(String resource, Map<String, Object> initContextParams) throws IOException
	{
		InputStream is = ActionPlanExecutor.class.getResourceAsStream(resource);
		boolean res = executeActionPlan(getName(resource), is, initContextParams);
		is.close();
		
		return res;
	}

	/**
	 * Executes specified action plan (obtained from specified input stream).
	 * @param planName under which reports would be generated
	 * @param is action input stream to execute.
	 * @param initContextParams Initial context params to be populated on context.
	 */
	public boolean executeActionPlan(String planName, InputStream is, Map<String, Object> initContextParams)
	{
		File reportFolder = new File(outputFolderPath, planName);
		
		try
		{
			FileUtils.forceMkdir(reportFolder);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to create report folder: {}", reportFolder.getPath());
		}
		
		context.setReportFolder(reportFolder);
		
		//clean current context
		context.clearStepGroups();
		context.clearAttributes();
		
		//load the action plan
		ActionPlanFile actionPlanFile = new ActionPlanFile(context);
		XMLBeanParser.parse(is, actionPlanFile, testSuiteParserHandler);

		ActionPlan actionPlan = actionPlanFile.getActionPlan();
		
		//create object required for execution
		ExecutionLogger exeLogger = new ExecutionLogger(context, "Ide", "Ide");
		TestCase dummy = new TestCase();
		dummy.setName("action-plan");
		
		TestSuite testSuite = new TestSuite();
		testSuite.setName("action-plan-test-suite");
		testSuite.addTestCase(dummy);
		
		//populate init context params
		for(String name : initContextParams.keySet())
		{
			context.setAttribute(name, initContextParams.get(name));
		}
		
		context.startLogMonitoring();
		
		boolean result = true;
		
		//execute action plan steps
		for(IStep step : actionPlan.getSteps())
		{
			try
			{
				StepExecutor.executeStep(context, exeLogger, step);
			} catch(Exception ex)
			{
				result = false;
				StepExecutor.handleException(context, dummy, step, exeLogger, ex, null);
				break;
			}
		}
		
		//Generate report
		TestCaseResult testCaseResult = null;
		
		if(result)
		{
			testCaseResult = new TestCaseResult("action-plan", TestStatus.SUCCESSFUL, exeLogger.getExecutionLogData(), null);
		}
		else
		{
			testCaseResult = new TestCaseResult("action-plan", TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Action plan execution failed.");
		}
		
		FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
		fullExecutionDetails.setReportName(context.getAppConfiguration().getReportName());
		fullExecutionDetails.addTestResult(testSuite, testCaseResult);
		
		if(result)
		{
			fullExecutionDetails.testSuiteCompleted(testSuite);
		}
		else
		{
			fullExecutionDetails.testSuiteFailed(testSuite, "Failed");
		}
		
		//stop monitoring logs
		Map<String, File> monitoringLogs = context.stopLogMonitoring();

		reportGenerator.createLogFiles(context, testCaseResult, "action-plan", monitoringLogs, "Action output");

		reportGenerator.generateReports(reportFolder, fullExecutionDetails, context);
		return result;
	}
}
