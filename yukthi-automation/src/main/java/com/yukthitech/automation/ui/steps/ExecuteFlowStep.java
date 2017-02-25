package com.yukthitech.automation.ui.steps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.Flow;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.State;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;

/**
 * Goes to the specified page url.
 * 
 * @author akiran
 */
@Executable("executeFlow")
public class ExecuteFlowStep implements IStep
{
	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(ExecuteFlowStep.class);

	/** 
	 * The error message. 
	 **/
	private static String ERROR_MESSAGE = "No flow found with name '{}' under state - {}";
	
	/**
	 * Flow under which this step is defined.
	 */
	protected Flow parentFlow;

	/**
	 * Sibling flow which needs to be executed.
	 */
	private String flow;

	/**
	 * Steps which takes to "flow" state before executing flow's steps.
	 */
	private GotoStateStep gotoStateStep;

	/**
	 * Instantiates a new execute flow step.
	 */
	public ExecuteFlowStep()
	{}

	/**
	 * Instantiates a new execute flow step.
	 *
	 * @param flow
	 *            the flow
	 */
	public ExecuteFlowStep(String flow)
	{
		this.flow = flow;
	}

	/**
	 * Executes the steps from specified flow.
	 * 
	 * @param context
	 *            Current automation context
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		Flow flow = getParentState().getFlow(this.flow);

		if(flow == null)
		{
			exeLogger.error(ERROR_MESSAGE, flow, getParentState().getName());
			throw new InvalidConfigurationException(ERROR_MESSAGE, flow, getParentState().getName());
		}

		// if goto state is not defined, define it
		if(gotoStateStep == null)
		{
			gotoStateStep = new GotoStateStep();
			gotoStateStep.setState(this.flow);
		}

		logger.trace("Executing flow: {}", flow);

		// execute goto state to ensure required state is achieved before
		// executing steps
		exeLogger.debug("Going to state - {}", this.flow);
		gotoStateStep.execute(context, exeLogger.getSubLogger());

		flow.getSteps().forEach(step -> {
			exeLogger.debug("Executing step - {}", step);
			step.execute(context, exeLogger.getSubLogger());
		});
	}

	/**
	 * Gets the sibling flow which needs to be executed.
	 *
	 * @return the sibling flow which needs to be executed
	 */
	public String getFlow()
	{
		return flow;
	}

	/**
	 * Sets the sibling flow which needs to be executed.
	 *
	 * @param flow
	 *            the new sibling flow which needs to be executed
	 */
	public void setFlow(String flow)
	{
		this.flow = flow;
	}

	/**
	 * Flow under which this step is defined.
	 * 
	 * @param parentFlow
	 *            Parent flow
	 */
	public void setParentFlow(Flow parentFlow)
	{
		this.parentFlow = parentFlow;
	}

	/**
	 * Fetches the parent state under which this step's parent flow is defined.
	 * 
	 * @return Parent state
	 */
	public State getParentState()
	{
		return parentFlow.getParentState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Flow: ").append(flow);

		builder.append("]");
		return builder.toString();
	}
}
