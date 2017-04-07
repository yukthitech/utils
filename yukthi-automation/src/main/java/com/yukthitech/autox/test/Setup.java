package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents list of steps that needs to be executed before executing testing unit.
 */
public class Setup implements IStepContainer, Validateable
{
	/**
	 * Name for logger and other purposes.
	 */
	public static final String NAME = "setup";
	
	/**
	 * Steps for the test case.
	 */
	private List<IStep> steps = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IStepContainer#addStep(com.yukthitech.ui.automation.
	 * IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		if(steps == null)
		{
			steps = new ArrayList<IStep>();
		}

		steps.add(step);
	}

	/**
	 * Gets the steps for the test case.
	 *
	 * @return the steps for the test case
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}

	/**
	 * Execute.
	 *
	 * @param context
	 *            the context
	 * @return the test case result
	 */
	public TestCaseResult execute(AutomationContext context)
	{
		ExecutionLogger exeLogger = new ExecutionLogger(NAME, NAME);
		exeLogger.debug("Started setup process");
		
		// execute the steps involved
		for(IStep step : steps)
		{
			try
			{
				AutomationUtils.replaceExpressions(context, step);
				step.execute(context, exeLogger);
			} catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing step - " + step);

				return new TestCaseResult(NAME, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Step errored - " + step);
			}
		}

		exeLogger.debug("Completed setup process");
		return new TestCaseResult(NAME, TestStatus.SUCCESSFUL, exeLogger.getExecutionLogData(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.core.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(CollectionUtils.isEmpty(steps))
		{
			throw new ValidateException("No steps provided for setup");
		}
	}
}
