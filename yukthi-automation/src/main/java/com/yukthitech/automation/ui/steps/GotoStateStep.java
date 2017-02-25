package com.yukthitech.automation.ui.steps;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.Flow;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.State;
import com.yukthitech.automation.ui.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to take current context to specified state.
 * 
 * @author akiran
 */
@Executable("gotoState")
public class GotoStateStep implements IStep
{
	/** 
	 * The error message. 
	 **/
	private static String ERROR_MESSAGE = "Invalid state name specified: {}";
	
	/** 
	 * The error path message. 
	 **/
	private static String ERROR_PATH_MESSAGE = "No path found to target state '{}' from current state(s) - {}";
	
	/**
	 * Target state to which this step should take.
	 */
	private String state;

	/**
	 * Sets the target state to which this step should take.
	 *
	 * @param state
	 *            the new target state to which this step should take
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * Executes the steps specified "stepsFrom" and validates "targetState" is
	 * reached at end.
	 * 
	 * @param context
	 *            Used for step execution
	 * @param stepsFrom
	 *            Steps to be executed to reach target state
	 * @param targetState
	 *            Target state to be reached
	 * @param exeLogger
	 *            Logger to be used
	 */
	private void executeSteps(AutomationContext context, Flow stepsFrom, State targetState, IExecutionLogger exeLogger)
	{
		for(IStep step : stepsFrom.getSteps())
		{
			exeLogger.debug("Executing step - {}", step);
			step.execute(context, exeLogger.getSubLogger());
		}

		exeLogger.debug("Validating target state is reached");

		// fetch current states

		AutomationUtils.validateWithWait(() -> AutomationUtils.getCurrentStates(context).contains(targetState), AutomationUtils.FIVE_SECONDS, "Waiting for target state to be reached", new InvalidStateException("Target state '{}' not reached after execution of full path - {}. Found current states - {}", targetState, state, AutomationUtils.getCurrentStates(context)));
	}

	/**
	 * Takes the context to the state specified.
	 * 
	 * @param context
	 *            Current automation context
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		// get target state
		State targetState = context.getStateConfiguration().getState(state);

		// ensure valid state name is specified
		if(targetState == null)
		{
			exeLogger.error(ERROR_MESSAGE, state);
			throw new InvalidArgumentException(ERROR_MESSAGE, state);
		}

		// fetch current states
		List<State> currentStates = AutomationUtils.getCurrentStates(context);

		// if current content is already in target state
		if(currentStates.contains(targetState))
		{
			exeLogger.debug("Current context is already in state - {}. No further action taken by this step.", state);
			return;
		}

		Flow stepsFrom = null;
		List<String> currentStateNames = new ArrayList<String>(currentStates.size());

		exeLogger.debug("Trying to move to state - {}. Current States: {}", targetState.getName(), currentStates);
		
		// loop through current state and check if target state path can be
		// found
		for(State curState : currentStates)
		{
			currentStateNames.add(curState.getName());
			stepsFrom = targetState.getFlow(curState.getName());

			// if path is found to target state
			if(stepsFrom != null)
			{
				exeLogger.debug("Going to state {} from current state {}", state, curState.getName());

				executeSteps(context, stepsFrom, targetState, exeLogger);
				return;
			}
		}

		exeLogger.error(ERROR_PATH_MESSAGE, state, currentStateNames);
		throw new InvalidStateException(ERROR_PATH_MESSAGE, state, currentStateNames);
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

		builder.append("State: ").append(state);

		builder.append("]");
		return builder.toString();
	}
}
