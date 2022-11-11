package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.exec.report.ReportManager;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;

public abstract class BaseExecutor implements IExecutor
{
	private ReportManager reportManager = ReportManager.getInstance();
	
	protected AutomationContext automationContext;
	
	protected Setup setup;
	
	protected Cleanup cleanup;
	
	protected Setup beforeChild;
	
	protected Cleanup afterChild;

	protected int parallelCount;

	private List<BaseExecutor> childExecutors;
	
	protected List<IStep> childSteps;
	
	private BaseExecutor parentExecutor;
	
	private String code;
	
	private String name;
	
	private String description;
	
	protected BaseExecutor(AutomationContext automationContext, String code, String name, String description)
	{
		this.automationContext = automationContext;
		
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	protected void addChildExector(BaseExecutor executor)
	{
		if(this.childExecutors == null)
		{
			this.childExecutors = new ArrayList<>();
		}
		
		executor.parentExecutor = this;
		this.childExecutors.add(executor);
	}
	
	@Override
	public String getCode()
	{
		return code;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public String getDescription()
	{
		return description;
	}

	public void execute()
	{
		//Execute setup
		if(setup != null)
		{
			ExecutionLogger setupLogger = ReportManager.getInstance().getSetupExecutionLogger(automationContext, this);
			
			try
			{
				new StepsExecutor(automationContext, setupLogger, setup.getSteps()).execute();
				reportManager.setupCompleted(automationContext, parentExecutor, setupLogger);
			}catch(Exception ex)
			{
				reportManager.setupErrored(automationContext, parentExecutor, setupLogger);
			}
		}
		
		try
		{
			//execute children
			if(childExecutors != null)
			{
				executeChildExecutors();
			}
			else
			{
				executeChildSteps();
			}
		} finally
		{
			//execute cleanup
			if(cleanup != null)
			{
				ExecutionLogger cleanupLogger = ReportManager.getInstance().getCleanupExecutionLogger(automationContext, this);
				
				try
				{
					new StepsExecutor(automationContext, cleanupLogger, setup.getSteps()).execute();
					reportManager.setupCompleted(automationContext, parentExecutor, cleanupLogger);
				}catch(Exception ex)
				{
					reportManager.setupErrored(automationContext, parentExecutor, cleanupLogger);
				}
			}
		}
	}

	private void executeChildExecutors()
	{
		ExecutionPool.getInstance().execute(childExecutors, parallelCount);
	}
	
	private void executeChildSteps()
	{
		ExecutionLogger logger = ReportManager.getInstance().getExecutionLogger(automationContext, this);
		new StepsExecutor(automationContext, logger, childSteps).execute();;
	}
}
