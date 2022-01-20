package com.yukthitech.autox.exec;

import com.yukthitech.autox.AutomationContext;

public interface IExecutable
{
	public ExecutionBranch buildExecutionBranch(AutomationContext context);
}
