package com.yukthitech.autox.ide.engine;

import com.yukthitech.autox.ide.model.ExecutedStep;

/**
 * Listener for ide engien.
 * @author akiran
 */
public interface IdeEngineListener
{
	/**
	 * Called when a new step is added.
	 * @param step added step.
	 */
	public default void executingStep(StepDetails step)
	{}
	
	/**
	 * Indicates other ui elements the output to be displayed.
	 * @param output
	 */
	public default void sendOutput(String output)
	{}
	
	/**
	 * Called when a new step is executed successfully and added to ide state.
	 * @param step
	 */
	public default void stepExecuted(ExecutedStep step)
	{}
	
	/**
	 * Called when existing state is loaded.
	 */
	public default void stateLoaded()
	{}
	
	/**
	 * Called when a step is removed.
	 * @param step
	 */
	public default void stepRemoved(ExecutedStep step)
	{}
}
