package com.yukthitech.autox.exec;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.Command;
import com.yukthitech.autox.event.AutomationEvent;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.ExpectedException;
import com.yukthitech.autox.test.FullExecutionDetails;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.ReportGenerator;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.StepExecutor;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestCaseResult;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.autox.test.TestSuiteResults;
import com.yukthitech.autox.test.lang.steps.ReturnException;

public class AutomationExecutorState
{
	private static Logger logger = LogManager.getLogger(AutomationExecutorState.class);
	
	private AutomationContext context;
	
	private LinkedList<ExecutionStackEntry> branchStack = new LinkedList<>();
	
	/**
	 * Used for generating reports.
	 */
	private ReportGenerator reportGenerator = new ReportGenerator();
	
	/**
	 * Holds execution details.
	 */
	private FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
	
	private TestSuite currentTestSuite;
	
	private long currentTestCaseId;
	
	private long testCaseStartedOn;
	
	private ExecutionLogger executionLogger;
	
	public AutomationExecutorState(AutomationContext context)
	{
		this.context = context;
		fullExecutionDetails.setReportName(context.getAppConfiguration().getReportName());
	}
	
	public boolean isSuccessful()
	{
		return fullExecutionDetails.isSetupSuccessful() 
				&& fullExecutionDetails.getTestSuiteSuccessCount() == fullExecutionDetails.getTestSuiteCount()
				&& fullExecutionDetails.isCleanupSuccessful();
	}
	
	public void pushBranch(ExecutionStackEntry entry)
	{
		this.branchStack.push(entry);
	}
	
	public boolean hasMoreBranches()
	{
		return !branchStack.isEmpty();
	}
	
	public ExecutionStackEntry peekBranch()
	{
		return branchStack.peek();
	}
	
	public ExecutionStackEntry popBranch()
	{
		return branchStack.pop();
	}
	
	public ExecutionStackEntry parentBranch()
	{
		int size = branchStack.size();
		
		if(size <= 1)
		{
			return null;
		}
		
		return branchStack.get(size - 2);
	}
	
	public ExecutionLogger getExecutionLogger()
	{
		return executionLogger;
	}
	
	public void automationCompleted()
	{
		this.fullExecutionDetails.setEndTime(new Date());
		
		//execute post commands if any
		List<Command> postCommands = context.getAppConfiguration().getPostCommands();
		
		for(Command command : postCommands)
		{
			logger.debug("Executing post-command: {}", command.getName());
			
			command.execute(context, new Command.ICommandLogger()
			{
				@Override
				public void warn(String mssg, Object... args)
				{
					logger.warn(mssg, args);
				}
				
				@Override
				public void info(String mssg, Object... args)
				{
					logger.info(mssg, args);
				}
				
				@Override
				public void debug(String mssg, Object... args)
				{
					logger.debug(mssg, args);
				}
				
				@Override
				public void output(String line)
				{
					logger.debug("[CMDLOG] [{}] - {}", command.getName(), line);
				}
			});
		}
		
		//create final report files
		reportGenerator.generateReports(context.getReportFolder(), fullExecutionDetails, context);
		
		System.out.println(String.format(
			"[Total Test Cases: %s, Successful: %s, Failed: %s, Errored: %s, Skipped: %s]", 
			fullExecutionDetails.getTestCaseCount(), 
			fullExecutionDetails.getTestCaseSuccessCount(), 
			fullExecutionDetails.getTestCaseFailureCount(),
			fullExecutionDetails.getTestCaseErroredCount(),
			fullExecutionDetails.getTestCaseSkippedCount()
		));
	}
	
	public void startedSteps(ExecutionBranch stepsBranch)
	{
		//if logger already exists, return the same
		if(executionLogger != null)
		{
			return;
		}
		
		//create a new logger based on parent
		ExecutionStackEntry parentEntry = branchStack.get(branchStack.size() - 2);
		ExecutionBranch parentBranch = parentEntry.getBranch(); 
		
		executionLogger = new ExecutionLogger(context, parentBranch.label, parentBranch.description);
	}

	public void started(ExecutionBranch branch)
	{
		if(branch.executable instanceof TestSuite)
		{
			testSuiteStarted((TestSuite) branch.executable);
		}
		else if(branch.executable instanceof TestCase)
		{
			testCaseStarted((TestCase) branch.executable);
		}
	}
	
	public void preSetup(ExecutionBranch branch)
	{
		//for non testcase startup new log needs to be created
		if(!(branch.executable instanceof TestCase))
		{
			this.executionLogger = new ExecutionLogger(context, Setup.NAME, Setup.NAME);
		}

		executionLogger.setMode("setup");
		context.getExecutionStack().push(branch.setup.executable);
		
		executionLogger.debug("Started setup process");
		context.setSetupExecution(true);
		context.setExecutionLogger(executionLogger);
	}
	
	public boolean postSetup(ExecutionStackEntry entry, ExecutionBranch branch, boolean successful)
	{
		context.getExecutionStack().pop(branch.setup.executable);
		
		context.setSetupExecution(false);
		executionLogger.clearMode();

		if(branch.executable instanceof TestCase)
		{
			return testCasePostStartup((TestCase) branch.executable, branch);
		}
		//for non-test case startup create required logs
		else
		{
			TestCaseResult res = null;
			
			if(successful)
			{
				res = new TestCaseResult(null, Setup.NAME, TestStatus.SUCCESSFUL, executionLogger.getExecutionLogData(), null,
					entry.getStartedOn(), new Date());
			}
			else
			{
				res = new TestCaseResult(null, Setup.NAME, TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Step errored",
						entry.getStartedOn(), new Date());
			}
			
			reportGenerator.createLogFiles(context, res, branch.getLabel() + "-setup", null, "");
		}
		
		return true;
	}
	
	public void preCleanup(ExecutionBranch branch)
	{
		//for non testcase startup new log needs to be created
		if(!(branch.executable instanceof TestCase))
		{
			this.executionLogger = new ExecutionLogger(context, Cleanup.NAME, Cleanup.NAME);
		}

		executionLogger.setMode("cleanup");
		context.getExecutionStack().push(branch.cleanup.executable);
		
		executionLogger.debug("Started cleanup process");
		context.setCleanupExecution(true);
		context.setExecutionLogger(executionLogger);
	}
	
	public boolean postCleanup(ExecutionStackEntry entry, ExecutionBranch branch, boolean successful)
	{
		context.getExecutionStack().pop(branch.cleanup.executable);
		
		context.setCleanupExecution(false);
		executionLogger.clearMode();

		//for non-test case startup create required logs
		if(!(branch.executable instanceof TestCase))
		{
			TestCaseResult res = null;
			
			if(successful)
			{
				res = new TestCaseResult(null, Cleanup.NAME, TestStatus.SUCCESSFUL, executionLogger.getExecutionLogData(), null,
					entry.getStartedOn(), new Date());
			}
			else
			{
				res = new TestCaseResult(null, Cleanup.NAME, TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Step errored",
						entry.getStartedOn(), new Date());
			}
			
			reportGenerator.createLogFiles(context, res, branch.getLabel() + "-cleanup", null, "");
		}
		
		return true;
	}

	public void completed(ExecutionStackEntry stackEntry)
	{
		ExecutionBranch branch = stackEntry.getBranch();
		boolean successful = (branch.result == null || branch.result.getStatus() == TestStatus.SUCCESSFUL);
		
		if(branch.executable instanceof TestSuite)
		{
			testSuiteCompleted((TestSuite) branch.executable, successful, branch);
		}
		else if(branch.executable instanceof TestCase)
		{
			if(branch.result == null)
			{
				branch.result = new TestCaseResult( (TestCase) branch.executable, branch.label, TestStatus.SUCCESSFUL, executionLogger.getExecutionLogData(), null,
						stackEntry.getStartedOn(), new Date());
			}

			testCaseCompleted((TestCase) branch.executable, successful, branch);
		}
	}
	
	public void handleException(ExecutionStackEntry entry, ExecutionFlowFailed flowEx)
	{
		Exception actualException = (Exception) flowEx.getCause();
		
		if(entry.getExceptionHandler() != null)
		{
			if(entry.getExceptionHandler().handleError(actualException))
			{
				return;
			}
		}
		
		if(actualException instanceof ReturnException)
		{
			ExecutionStackEntry stackEntry = branchStack.pop();
			completed(stackEntry);
			return;
		}
		
		logger.error("Execution failed with error: ", actualException);
		this.executionLogger.error("An error occurred with message - {}. Stack Trace: {}", actualException.getMessage(), context.getExecutionStack().toStackTrace());
		
		ExecutionStackEntry stackEntry = null;
		TestCaseResult result = null;
		boolean setupFailed = false;
		boolean cleanupFailed = false;
		
		while(!this.branchStack.isEmpty())
		{
			stackEntry = this.branchStack.pop();
			
			if(stackEntry.isStepsEntry())
			{
				continue;
			}
			
			ExecutionBranch branch = stackEntry.getBranch();
			
			if(branch.executable instanceof Setup)
			{
				postSetup(stackEntry, branch, false);
				setupFailed = true;
				continue;
			}
			
			if(branch.executable instanceof Cleanup)
			{
				cleanupFailed = true;
				continue;
			}
			
			if(branch.executable instanceof TestCase)
			{
				result = handleTestCaseError(branch, setupFailed, cleanupFailed, stackEntry, flowEx);
				branch.result = result;
				
				//push then entry back so that cleanup and other things can be executed
				this.branchStack.push(stackEntry);
				
				if(result.getStatus() != TestStatus.SUCCESSFUL)
				{
					Iterator<ExecutionStackEntry> it = this.branchStack.descendingIterator();
					
					while(it.hasNext())
					{
						ExecutionStackEntry subentry = it.next();
						
						if(subentry.getBranch().getExecutable() instanceof TestSuite)
						{
							subentry.getBranch().incrementFailedChildCount();
						}
					}
					
					break;
				}
				
				break;
			}
			else if(branch.executable instanceof TestSuite)
			{
				TestSuite testSuite = (TestSuite) branch.executable;
				
				if(setupFailed)
				{
					TestSuiteResults results = fullExecutionDetails.testSuiteFailed(testSuite, "Setup execution failed.");
					results.setSetupSuccessful(false);
					break;
				}
				
				if(cleanupFailed)
				{
					TestSuiteResults results = fullExecutionDetails.testSuiteFailed(testSuite, "Cleanup execution failed.");
					results.setCleanupSuccessful(false);
					break;
				}
			}
			else if(branch.executable instanceof TestSuiteGroup)
			{
				if(setupFailed)
				{
					result = StepExecutor.handleException(context, new TestCase("setup"), flowEx.getSourceStep(), 
							executionLogger, actualException, null, stackEntry.getStartedOn());
					
					if(result == null)
					{
						new TestCaseResult(null, "setup", TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Step errored - " + flowEx.getSourceStep(),
								stackEntry.getStartedOn(), new Date());
					}
					
					
					fullExecutionDetails.setSetupSuccessful(false);
					reportGenerator.createLogFiles(context, result, "_global-setup", null, "");
					break;
				}
				
				if(cleanupFailed)
				{
					result = StepExecutor.handleException(context, new TestCase("cleanup"), flowEx.getSourceStep(), 
							executionLogger, actualException, null, stackEntry.getStartedOn());
					
					if(result == null)
					{
						new TestCaseResult(null, "cleanup", TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Step errored - " + flowEx.getSourceStep(),
								stackEntry.getStartedOn(), new Date());
					}
					
					
					fullExecutionDetails.setCleanupSuccessful(false);
					reportGenerator.createLogFiles(context, result, "_global-cleanup", null, "");
					break;
				}
			}
		}
	}
	
	private TestCaseResult handleTestCaseError(ExecutionBranch branch, boolean setupFailed, boolean cleanupFailed, ExecutionStackEntry stackEntry, ExecutionFlowFailed flowEx)
	{
		TestCase testCase = (TestCase) branch.executable;
		
		if(setupFailed)
		{
			return new TestCaseResult(testCase, branch.label, TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Setup execution failed.",
					stackEntry.getStartedOn(), new Date());
		}
		
		if(cleanupFailed)
		{
			return new TestCaseResult(testCase, branch.label, TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Cleanup execution failed.",
					stackEntry.getStartedOn(), new Date());
		}
		
		ExpectedException expectedException = null;
		
		if(testCase.getExpectedException() != null)
		{
			expectedException = testCase.getExpectedException().clone();
			AutomationUtils.replaceExpressions("expectedException", context, expectedException);
			
			//if expected exception is disabled, ignore the expected exception
			if(!"true".equals(expectedException.getEnabled()))
			{
				expectedException = null;
			}
		}
		
		Exception actualException = (Exception) flowEx.getCause();
		TestCaseResult result = StepExecutor.handleException(context, testCase, branch.label, flowEx.getSourceStep(), 
				executionLogger, actualException, expectedException, stackEntry.getStartedOn());
		
		//during expected exception occurance result will be null
		if(expectedException != null && result == null)
		{
			return new TestCaseResult(testCase, branch.label, TestStatus.SUCCESSFUL, executionLogger.getExecutionLogData(), null,
					stackEntry.getStartedOn(), new Date());
		}
		
		return result;
	}
	
	private void testSuiteStarted(TestSuite testSuite)
	{
		logger.debug("Executing test suite - {}", testSuite.getName());
		
		this.currentTestSuite = testSuite;
		fullExecutionDetails.testSuiteInProgress(testSuite);

		//set test suite as active test suite
		context.setActiveTestSuite(testSuite);
		
		//for interactive environment set the test suite on interactive context
		// last test suite to be set when testcase is being executed (thats why it is kept inside loop)
		if(context.getInteractiveEnvironmentContext() != null)
		{
			context.getInteractiveEnvironmentContext().setLastTestSuite(testSuite);
		}
	}

	private void testSuiteCompleted(TestSuite testSuite, boolean successful, ExecutionBranch branch)
	{
		if(successful)
		{
			if(branch.failedChildCount > 0)
			{
				fullExecutionDetails.testSuiteFailed(testSuite, "Failing as one or more test cases failed");
			}
			else
			{
				TestSuiteResults results = fullExecutionDetails.testSuiteCompleted(testSuite);
				results.setSetupSuccessful(true);
				results.setCleanupSuccessful(true);
			}
		}
		
		this.currentTestSuite = null;
	}
	
	private void testCaseStarted(TestCase testCase)
	{
		logger.debug("Executing test case '{}' in test suite - {}", testCase.getName(), currentTestSuite.getName());
		fullExecutionDetails.startedTestCase(testCase.getName());
		
		//add new execution to store
		if(context.getPersistenceStorage() != null)
		{
			currentTestCaseId = context.getPersistenceStorage().testCaseStarted(currentTestSuite.getName(), testCase.getName());
		}
		
		testCaseStartedOn = System.currentTimeMillis();

		context.getAutomationListener().testCaseStarted(new AutomationEvent(currentTestSuite, testCase, null));
		
		//start monitoring logs
		context.startLogMonitoring();
		context.setActiveTestCase(testCase, null);
		context.getExecutionStack().push(testCase);
	}
	
	private boolean testCasePostStartup(TestCase testCase, ExecutionBranch branch)
	{
		IDataProvider dataProvider = testCase.getDataProvider();
		
		if(dataProvider == null)
		{
			return true;
		}
		
		ExecutionBranch dataProviderBranch = new ExecutionBranch(testCase.getName() + " - dataProvider", testCase.getDescription(), testCase);
		dataProviderBranch.setup = new ExecutionBranch(testCase.getName() + " [[Data-Setup]]", "Data Setup", testCase.getDataSetup());
		dataProviderBranch.cleanup = new ExecutionBranch(testCase.getName() + " [[Data-Cleanup]]", "Data Cleanup", testCase.getDataCleanup());
		
		List<TestCaseData> dataLst = null;
		Date startTime = new Date();
		context.setActiveTestCase(testCase, null);
		
		try
		{
			dataLst = dataProvider.getStepData();
		} finally
		{
			context.clearActiveTestCase();
		}
		
		if(CollectionUtils.isEmpty(dataLst))
		{
			branch.result = new TestCaseResult(testCase, TestStatus.ERRORED, null, "No data from data-provider. Data Provider: " + dataProvider.getName(),
					startTime, new Date());
			return false;
		}
		
		for(TestCaseData data : dataLst)
		{
			String description = data.getDescription();
			description = StringUtils.isNotBlank(description)? description : testCase.getDescription(); 

			ExecutionBranch dpTestBranch = new ExecutionBranch(testCase.getName() + " [" + data.getName() + "]", 
					description, testCase);
			
			dataProviderBranch.addChildBranch(dpTestBranch);
		}
		
		branch.addChildBranch(dataProviderBranch);
		branch.dataBranch = true;
		return false;
	}
	
	private void testCaseCompleted(TestCase testCase, boolean successful, ExecutionBranch branch)
	{
		if(context.getPersistenceStorage() != null)
		{
			context.getPersistenceStorage().updateExecution(currentTestCaseId, 
					successful, 
					(System.currentTimeMillis() - testCaseStartedOn), 
					branch.result.getMessage());
		}

		fullExecutionDetails.addTestResult(currentTestSuite, branch.result);

		context.getExecutionStack().pop(testCase);
		context.clearActiveTestCase();

		//stop monitoring logs
		Map<String, File> monitoringLogs = context.stopLogMonitoring(branch.result);
		String testFileName = currentTestSuite.getName() + "_" + testCase.getName();
		reportGenerator.createLogFiles(context, branch.result, testFileName, monitoringLogs, testCase.getDescription());
		
		context.getAutomationListener().testCaseCompleted(new AutomationEvent(currentTestSuite, testCase, branch.result));
	}
}
