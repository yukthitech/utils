package com.yukthitech.autox.exec;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
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
	
	private LinkedList<ExecutionStackEntry> executionStack = new LinkedList<>();
	
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
	
	/**
	 * Used as suffix for data provider logs.
	 */
	private AtomicInteger logIndex = new AtomicInteger(1);
	
	private Set<String> completedTestCases = new HashSet<String>();
	
	public AutomationExecutorState(AutomationContext context)
	{
		this.context = context;
		fullExecutionDetails.setReportName(context.getAppConfiguration().getReportName());
	}
	
	public boolean isCompleted(String testCase)
	{
		String tsName = currentTestSuite.getName() + ".";
		return completedTestCases.contains(tsName + testCase);
	}
	
	public boolean isSuccessful()
	{
		return fullExecutionDetails.isSetupSuccessful() 
				&& fullExecutionDetails.getTestSuiteSuccessCount() == fullExecutionDetails.getTestSuiteCount()
				&& fullExecutionDetails.isCleanupSuccessful();
	}
	
	public void pushBranch(ExecutionStackEntry entry)
	{
		ExecutionStackEntry curEntry = executionStack.peek();
		
		entry.setParent(curEntry);
		this.executionStack.push(entry);
		setExecutionLoggerFor(entry);
		
		if(entry.getOnInit() != null)
		{
			entry.getOnInit().accept(entry);
		}
	}
	
	public boolean hasMoreBranches()
	{
		return !executionStack.isEmpty();
	}
	
	public ExecutionStackEntry peekBranch()
	{
		return executionStack.peek();
	}
	
	public ExecutionStackEntry popBranch()
	{
		return executionStack.pop();
	}
	
	public ExecutionStackEntry parentBranch()
	{
		int size = executionStack.size();
		
		if(size <= 1)
		{
			return null;
		}
		
		return executionStack.get(1);
	}
	
	private void setExecutionLoggerFor(ExecutionStackEntry entry)
	{
		ExecutionLogger executionLogger = null;
		ExecutionStackEntry loggableEntry = entry.getParentEntry(Setup.class, Cleanup.class, TestCase.class);
		
		//for top entries, no logger is needed
		if(loggableEntry == null)
		{
			return;
		}
		
		executionLogger = loggableEntry.getExecutionLogger();
		
		if(executionLogger != null)
		{
			entry.setExecutionLogger(executionLogger);
			return;
		}
		
		Object executable = entry.getExecutable();
		
		if((executable instanceof Setup) || (executable instanceof Cleanup))
		{
			ExecutionStackEntry testCaseEntry = loggableEntry.getParentEntry(TestCase.class);
			
			//test case branch null, indicates setup belongs to global or test suite
			if(testCaseEntry == null)
			{
				//for non testcase startup new log needs to be created
				String name = (executable instanceof Setup) ? Setup.NAME : Cleanup.NAME;
				executionLogger = new ExecutionLogger(context, name, name);
			}
			else
			{
				executionLogger = testCaseEntry.getExecutionLogger();
			}
		}
		//for testcase
		else
		{
			TestCase testCase = (TestCase) executable;
			executionLogger = new ExecutionLogger(context, testCase.getName(), testCase.getDescription());
		}
		
		
		entry.setExecutionLogger(executionLogger);
	}
	
	public ExecutionLogger getExecutionLogger()
	{
		ExecutionStackEntry entry = executionStack.peek();
		return entry.getExecutionLogger();
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
		/*
		//if logger already exists, return the same
		if(executionLogger != null)
		{
			return;
		}
		
		//create a new logger based on parent
		ExecutionStackEntry parentEntry = branchStack.get(branchStack.size() - 2);
		ExecutionBranch parentBranch = parentEntry.getBranch(); 
		
		executionLogger = new ExecutionLogger(context, parentBranch.label, parentBranch.description);
		*/
	}

	public void started(ExecutionBranch branch)
	{
		if(branch.executable instanceof TestSuite)
		{
			testSuiteStarted((TestSuite) branch.executable);
		}
		else if(branch.executable instanceof TestCase)
		{
			testCaseStarted((TestCase) branch.executable, branch);
		}
	}
	
	public void startMode(String mode)
	{
		ExecutionLogger executionLogger = getExecutionLogger();
		executionLogger.setMode(mode);
	}
	
	public void clearMode()
	{
		ExecutionLogger executionLogger = getExecutionLogger();
		executionLogger.clearMode();
	}
	
	public void preSetup(ExecutionBranch branch)
	{
		ExecutionLogger executionLogger = getExecutionLogger();
		executionLogger.setMode("setup");
		context.getExecutionStack().push(branch.setup.executable);
		
		executionLogger.debug("Started setup process");
		context.setSetupExecution(true);
		context.setExecutionLogger(executionLogger);
	}
	
	public boolean postSetup(ExecutionStackEntry entry, ExecutionBranch parentBranch, boolean successful)
	{
		ExecutionLogger executionLogger = getExecutionLogger();
		
		//Note: Entry will be null, when setup is not present
		if(entry != null)
		{
			context.getExecutionStack().pop(parentBranch.setup.executable);
			
			context.setSetupExecution(false);
			executionLogger.clearMode();
		}

		//for non-test case startup create required logs
		if(!(parentBranch.executable instanceof TestCase))
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
			
			reportGenerator.createLogFiles(context, res, parentBranch.getLabel() + "-setup", null, "");
		}
		
		return true;
	}
	
	public void preCleanup(ExecutionBranch branch)
	{
		ExecutionLogger executionLogger = getExecutionLogger();
		
		executionLogger.setMode("cleanup");
		context.getExecutionStack().push(branch.cleanup.executable);
		
		executionLogger.debug("Started cleanup process");
		context.setCleanupExecution(true);
		context.setExecutionLogger(executionLogger);
	}
	
	public boolean postCleanup(ExecutionStackEntry entry, ExecutionBranch parentBranch, boolean successful)
	{
		if(entry == null)
		{
			return true;
		}
		
		ExecutionLogger executionLogger = getExecutionLogger();
		
		context.getExecutionStack().pop(parentBranch.cleanup.executable);
		
		context.setCleanupExecution(false);
		executionLogger.clearMode();

		//for non-test case startup create required logs
		if(!(parentBranch.executable instanceof TestCase))
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
			
			reportGenerator.createLogFiles(context, res, parentBranch.getLabel() + "-cleanup", null, "");
			executionLogger = null;
		}
		
		return true;
	}

	public void completedBranchEntry(ExecutionStackEntry stackEntry)
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
				ExecutionLogger executionLogger = getExecutionLogger();
				
				branch.result = new TestCaseResult( (TestCase) branch.executable, branch.label, TestStatus.SUCCESSFUL, executionLogger.getExecutionLogData(), null,
						stackEntry.getStartedOn(), new Date());
			}

			testCaseCompleted((TestCase) branch.executable, successful, branch);
		}
	}
	
	public void handleException(ExecutionStackEntry entry, IStep sourceStep, Exception actualException)
	{
		if(entry.getExceptionHandler() != null)
		{
			if(entry.getExceptionHandler().handleError(actualException))
			{
				return;
			}
		}
		
		if(actualException instanceof ReturnException)
		{
			ExecutionStackEntry stackEntry = executionStack.pop();
			completedBranchEntry(stackEntry);
			return;
		}
		
		ExecutionLogger executionLogger = getExecutionLogger();
		
		executionLogger.error(actualException, "An error occurred with message - {}", actualException.getMessage());
		
		ExecutionStackEntry stackEntry = null;
		TestCaseResult result = null;
		boolean setupFailed = false;
		boolean cleanupFailed = false;
		
		ExecutionType setupType = null;
		ExecutionType cleanupType = null;
		
		while(!this.executionStack.isEmpty())
		{
			stackEntry = this.executionStack.peek();
			
			Object executable = stackEntry.getExecutable();
			
			if(executable instanceof Setup)
			{
				if(stackEntry.getExecutionType() == ExecutionType.SETUP)
				{
					postSetup(stackEntry, stackEntry.getParent().getBranch(), false);
				}
				
				setupFailed = true;
				setupType = stackEntry.getExecutionType();
				stackEntry.completedBranch(false);
				
				this.executionStack.pop();
				continue;
			}
			
			if(executable instanceof Cleanup)
			{
				if(stackEntry.getExecutionType() == ExecutionType.CLEANUP)
				{
					postCleanup(stackEntry, stackEntry.getParent().getBranch(), false);
				}
				
				cleanupFailed = true;
				cleanupType = stackEntry.getExecutionType();
				stackEntry.completedBranch(false);
				
				this.executionStack.pop();
				continue;
			}
			
			if(stackEntry.isStepsEntry())
			{
				stackEntry.completedBranch(false);
				this.executionStack.pop();
				continue;
			}
			
			if(executable instanceof TestCase)
			{
				ExecutionBranch branch = stackEntry.getBranch();
				
				result = handleTestCaseError(branch, setupFailed, cleanupFailed, stackEntry, sourceStep, 
						actualException, setupType, cleanupType);
				branch.result = result;
				
				if(result.getStatus() != TestStatus.SUCCESSFUL)
				{
					ExecutionStackEntry subentry = stackEntry.getParent();

					while(subentry != null)
					{
						if(subentry.getBranch() == null)
						{
							continue;
						}
						
						if(subentry.getBranch() != null && (subentry.getBranch().getExecutable() instanceof TestSuite))
						{
							subentry.getBranch().incrementFailedChildCount();
							break;
						}
						
						subentry = subentry.getParent();
					}
				}
				
				//if setup is failed further execution should not happen
				if(setupFailed)
				{
					completedBranchEntry(stackEntry);
					stackEntry.completedBranch(false);
					this.executionStack.pop();
				}
				
				break;
			}
			else if(executable instanceof TestSuite)
			{
				TestSuite testSuite = (TestSuite) executable;
				
				if(setupFailed)
				{
					TestSuiteResults results = fullExecutionDetails.testSuiteFailed(testSuite, "Setup execution failed.");
					results.setSetupSuccessful(false);
					results.setCleanupSuccessful(true);
					
					stackEntry.completedBranch(false);
					
					//remove test suite entry, as setup itself failed
					this.executionStack.pop();
					break;
				}
				
				if(cleanupFailed)
				{
					TestSuiteResults results = fullExecutionDetails.testSuiteFailed(testSuite, "Cleanup execution failed.");
					results.setSetupSuccessful(true);
					results.setCleanupSuccessful(false);
					
					stackEntry.completedBranch(false);

					//remove test suite entry, as setup itself failed
					this.executionStack.pop();
					break;
				}
			}
			else if(executable instanceof TestSuiteGroup)
			{
				if(setupFailed)
				{
					result = StepExecutor.handleException(context, new TestCase("setup"), sourceStep, 
							executionLogger, actualException, null, stackEntry.getStartedOn());
					
					if(result == null)
					{
						new TestCaseResult(null, "setup", TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Step errored - " + sourceStep,
								stackEntry.getStartedOn(), new Date());
					}
					
					
					fullExecutionDetails.setSetupSuccessful(false);
					reportGenerator.createLogFiles(context, result, "_global-setup", null, "");
					
					stackEntry.completedBranch(false);
					break;
				}
				
				if(cleanupFailed)
				{
					result = StepExecutor.handleException(context, new TestCase("cleanup"), sourceStep, 
							executionLogger, actualException, null, stackEntry.getStartedOn());
					
					if(result == null)
					{
						new TestCaseResult(null, "cleanup", TestStatus.ERRORED, executionLogger.getExecutionLogData(), "Step errored - " + sourceStep,
								stackEntry.getStartedOn(), new Date());
					}
					
					
					fullExecutionDetails.setCleanupSuccessful(false);
					reportGenerator.createLogFiles(context, result, "_global-cleanup", null, "");
					
					stackEntry.completedBranch(false);
					break;
				}
			}
		}
	}
	
	private TestCaseResult handleTestCaseError(ExecutionBranch branch, boolean setupFailed, boolean cleanupFailed, ExecutionStackEntry stackEntry, 
			IStep sourceStep, Exception actualException, ExecutionType setupType, ExecutionType cleanupType)
	{
		TestCase testCase = (TestCase) branch.executable;
		ExecutionLogger executionLogger = getExecutionLogger();
		
		if(setupFailed)
		{
			String mssg = (setupType == ExecutionType.DATA_SETUP) ? "Data provider setup failed." : "Setup execution failed.";
			
			return new TestCaseResult(testCase, branch.label, TestStatus.ERRORED, executionLogger.getExecutionLogData(), mssg,
					stackEntry.getStartedOn(), new Date());
		}
		
		if(cleanupFailed)
		{
			String mssg = (cleanupType == ExecutionType.DATA_CLEANUP) ? "Data provider cleanup failed." : "Cleanup execution failed.";
			
			return new TestCaseResult(testCase, branch.label, TestStatus.ERRORED, executionLogger.getExecutionLogData(), mssg,
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
		
		TestCaseResult result = StepExecutor.handleException(context, testCase, branch.label, sourceStep, 
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
			TestSuiteResults results = null;
			
			if(branch.failedChildCount > 0)
			{
				results = fullExecutionDetails.testSuiteFailed(testSuite, "Failing as one or more test cases failed");
			}
			else
			{
				results = fullExecutionDetails.testSuiteCompleted(testSuite);
			}

			results.setSetupSuccessful(true);
			results.setCleanupSuccessful(true);
		}
		
		this.currentTestSuite = null;
	}
	
	private void testCaseStarted(TestCase testCase, ExecutionBranch branch)
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
		context.setActiveTestCase(testCase, branch.testCaseData);
		testCase.setData(branch.testCaseData);
		
		if(branch.testCaseData != null)
		{
			context.setAttribute(testCase.getDataProvider().getName(), branch.testCaseData.getValue());
		}
		
		context.getExecutionStack().push(testCase);
	}
	
	public boolean testCasePostDataStartup(TestCase testCase, ExecutionBranch testCaseBranch)
	{
		IDataProvider dataProvider = testCaseBranch.getDataProvider();
		
		if(dataProvider == null)
		{
			return true;
		}
		
		ExecutionStackEntry testSuiteEntry = parentBranch();
		
		//copy before child and after child branches, so that for every test case
		// execution these are executed approp
		testCaseBranch.beforeChild = testSuiteEntry.getBranch().beforeChild;
		testCaseBranch.afterChild = testSuiteEntry.getBranch().afterChild;
		
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
			testCaseBranch.result = new TestCaseResult(testCase, TestStatus.ERRORED, null, "No data from data-provider. Data Provider: " + dataProvider.getName(),
					startTime, new Date());
			return false;
		}
		
		for(TestCaseData data : dataLst)
		{
			String description = data.getDescription();
			description = StringUtils.isNotBlank(description)? description : testCase.getDescription(); 

			ExecutionBranch dpTestBranch = new ExecutionBranch(testCase.getName() + " [" + data.getName() + "]", 
					description, testCase);
			dpTestBranch.testCaseData = data;
			dpTestBranch.childSteps = testCaseBranch.childSteps;
			
			testCaseBranch.addChildBranch(dpTestBranch);
		}
		
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
		
		if(branch.testCaseData != null)
		{
			testFileName = testFileName + "_" + logIndex.getAndIncrement();
		}
		
		reportGenerator.createLogFiles(context, branch.result, testFileName, monitoringLogs, testCase.getDescription());
		
		context.getAutomationListener().testCaseCompleted(new AutomationEvent(currentTestSuite, testCase, branch.result));
		
		if(branch.result.getStatus() == TestStatus.SUCCESSFUL)
		{
			completedTestCases.add(currentTestSuite.getName() + "." + testCase.getName());
		}
	}
}
