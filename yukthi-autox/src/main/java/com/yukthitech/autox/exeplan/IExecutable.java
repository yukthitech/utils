package com.yukthitech.autox.exeplan;

public interface IExecutable
{
	public void execute(ExecutableExecutorPool executorPool, IExecuteCallback mainCallback);
}
