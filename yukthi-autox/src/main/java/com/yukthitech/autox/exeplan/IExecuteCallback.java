package com.yukthitech.autox.exeplan;

/**
 * Callback that can be used to get notification when target executable
 * execution is completed.
 * @author akiran
 */
public interface IExecuteCallback
{
	public void executionCompleted(ExecutableElement executable);
	
	public default void executionFailed(ExecutableElement executable)
	{}
}
