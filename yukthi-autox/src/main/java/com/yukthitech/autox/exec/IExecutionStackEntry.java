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
	
	public void resetChildIndex();
	
	public void skipChildSteps();
	
	public void setStepListener(IStepListener stepListener);
}
