package com.yukthitech.autox.exec;

import com.yukthitech.autox.IStepListener;

/**
 * Represents an entry on execution stack.
 * @author akranthikiran
 */
public interface IExecutionStackEntry
{
	/**
	 * Sets variable with specified name and value.
	 * @param name
	 * @param value
	 * @return Returns current instance for chaining.
	 */
	public IExecutionStackEntry setVariable(String name, Object value);
	
	/**
	 * Gets the variable with specified name.
	 * @param name
	 * @return
	 */
	public Object getVariable(String name);
	
	/**
	 * This will reset the child index to zero (that is from starting). As the step
	 * is starting itself, isReexecutionNeeded() method of step-execution will NOT be called.
	 */
	public void resetChildIndex();
	
	/**
	 * Skips the index to index after child steps. 
	 * This will make the executor look like current step is at end 
	 * and isReexecutionNeeded() method of step-execution will be called.
	 */
	public void skipChildSteps();
	
	public void setStepListener(IStepListener stepListener);
}
