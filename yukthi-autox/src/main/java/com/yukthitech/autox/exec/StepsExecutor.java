package com.yukthitech.autox.exec;

import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;

public class StepsExecutor
{
	private AutomationContext context;
	
	private ExecutionLogger logger;
	
	private List<IStep> steps;

	public StepsExecutor(AutomationContext context, ExecutionLogger logger, List<IStep> steps)
	{
		this.context = context;
		this.logger = logger;
		this.steps = steps;
	}
	
	public void execute()
	{
		
	}
}
