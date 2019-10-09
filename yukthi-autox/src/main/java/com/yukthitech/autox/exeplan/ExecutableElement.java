package com.yukthitech.autox.exeplan;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.utils.ObjectWrapper;

/**
 * Represents executable element of the execution plan.
 * @author akiran
 */
public class ExecutableElement implements IExecutable
{
	/**
	 * Name of this executable.
	 */
	private String name;
	
	/**
	 * Setup needed for this executable.
	 */
	private Setup setup;
	
	/**
	 * Actual executable object.
	 */
	private Object mainExecutable;
	
	/**
	 * Cleanup needed for this executable.
	 */
	private Cleanup cleanup;
	
	public ExecutableElement(String name, Setup setup, Object mainExecutable, Cleanup cleanup)
	{
		this.name = name;
		this.setup = setup;
		this.mainExecutable = mainExecutable;
		this.cleanup = cleanup;
	}

	public ExecutableElement(String name, Setup setup, List<ExecutableElement> mainExecutables, Cleanup cleanup)
	{
		this.name = name;
		this.setup = setup;
		this.mainExecutable = mainExecutables;
		this.cleanup = cleanup;
	}

	public void execute(ExecutableExecutorPool executorPool, IExecuteCallback mainCallback)
	{
		/*
		IExecuteCallback exeCallback = new IExecuteCallback() 
		{
			@Override
			public void executionCompleted(ExecutableElement executable)
			{
				executeChildList(executorPool, mainCallback);
			}

			@Override
			public void executionFailed(ExecutableElement executable)
			{
				if(mainCallback == null)
				{
					return;
				}
				
				mainCallback.executionFailed(executable);
			}
		};
		
		executorPool.execute(mainExecutable, exeCallback);
		*/
	}
}
