package com.yukthitech.autox.exeplan;

import java.util.List;

/**
 * List of executables to be executed.
 * @author akiran
 */
public class ExecutableGroup implements IExecutable
{
	/**
	 * List of executables to be executed.
	 */
	private List<ExecutableElement> executables;
	
	/**
	 * Flag indicating if execution should be parallel.
	 */
	private boolean parallel;

	public ExecutableGroup(List<ExecutableElement> executables, boolean parallel)
	{
		this.executables = executables;
		this.parallel = parallel;
	}

	@Override
	public void execute(ExecutableExecutorPool executorPool, IExecuteCallback mainCallback)
	{
	}
}
