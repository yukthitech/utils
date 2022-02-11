package com.yukthitech.autox.exec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.test.FullExecutionDetails;
import com.yukthitech.autox.test.ReportGenerator;
import com.yukthitech.autox.test.TestSuite;

public class AutomationExecutorState
{
	private static Logger logger = LogManager.getLogger(AutomationExecutorState.class);
	
	private AutomationContext context;
	
	/**
	 * Used for generating reports.
	 */
	private ReportGenerator reportGenerator = new ReportGenerator();
	
	/**
	 * Holds execution details.
	 */
	private FullExecutionDetails fullExecutionDetails = new FullExecutionDetails();
	
	public AutomationExecutorState(AutomationContext context)
	{
		this.context = context;
	}

	public void started(ExecutionBranch branch)
	{
		if(branch.executable instanceof TestSuite)
		{
			testSuiteStarted((TestSuite) branch.executable);
		}
	}
	
	public void completed(ExecutionBranch branch, boolean successful)
	{
		if(branch.executable instanceof TestSuite)
		{
			testSuiteCompleted((TestSuite) branch.executable, successful);
		}
	}
	
	private void testSuiteStarted(TestSuite testSuite)
	{
		logger.debug("Executing test suite - {}", testSuite.getName());
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

	private void testSuiteCompleted(TestSuite testSuite, boolean successful)
	{
		if(successful)
		{
			fullExecutionDetails.testSuiteCompleted(testSuite);
		}
		else
		{
			fullExecutionDetails.testSuiteFailed(testSuite, "Failing as one or more test cases failed");
		}
	}
}
