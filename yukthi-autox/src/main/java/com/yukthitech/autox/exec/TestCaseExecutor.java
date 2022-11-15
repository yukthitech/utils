package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TestCaseExecutor extends Executor
{
	private TestCase testCase;
	
	private TestCaseData testCaseData;
	
	private List<TestCaseExecutor> dependencies;
	
	private Setup dataSetup;
	
	private Cleanup dataCleanup;
	
	public TestCaseExecutor(TestCase testCase)
	{
		super(testCase);
		
		this.testCase = testCase;
		super.setup = testCase.getSetup();
		super.cleanup = testCase.getCleanup();
		
		if(testCase.getDataProvider() != null)
		{
			this.dataSetup = testCase.getDataSetup();
			this.dataCleanup = testCase.getDataCleanup();
			
			super.parallelCount = testCase.getParallelExecutionCount();
		}
		else
		{
			super.childSteps = testCase.getSteps();
			super.expectedException = testCase.getExpectedException();
		}
	}
	
	private TestCaseExecutor(TestCase testCase, TestCaseData testCaseData)
	{
		super(testCase);
		
		this.testCase = testCase;
		this.testCaseData = testCaseData;
		super.childSteps = testCase.getSteps();
		super.expectedException = testCase.getExpectedException();
	}
	
	public TestCase getTestCase()
	{
		return testCase;
	}
	
	public TestCaseData getTestCaseData()
	{
		return testCaseData;
	}
	
	public void addDependency(TestCaseExecutor executor)
	{
		if(executor == null)
		{
			throw new InvalidArgumentException("Executor cannot be null");
		}
		
		if(dependencies == null)
		{
			this.dependencies = new ArrayList<>();
		}
		
		this.dependencies.add(executor);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Executor> getDependencies()
	{
		return (List) dependencies;
	}
	
	@Override
	public boolean isReadyToExecute()
	{
		if(CollectionUtils.isEmpty(dependencies))
		{
			return true;
		}
		
		for(TestCaseExecutor dep : this.dependencies)
		{
			if(dep.status == null || dep.status == TestStatus.IN_PROGRESS)
			{
				return false;
			}
			
			if(dep.status == TestStatus.SKIPPED || dep.status.isErrored())
			{
				ReportDataManager.getInstance().executionSkipped(ExecutionType.MAIN, this, 
						String.format("Skipped as required dependency test-case '%s' is found with status: %s", 
								dep.testCase.getName(), dep.status));
				return false;
			}
		}
		
		return true;
	}
	
	private List<TestCaseData> executeDataProvider(IDataProvider dataProvider)
	{
		AutomationContext automationContext = AutomationContext.getInstance();
		
		automationContext.setActiveTestCase(testCase, null);
		
		IExecutionLogger executionLogger = ReportDataManager.getInstance().getSetupExecutionLogger(this);
		executionLogger.setMode("Data-Provider");
		automationContext.setExecutionLogger(executionLogger);
		
		try
		{
			return dataProvider.getStepData();
		} finally
		{
			automationContext.setExecutionLogger(null);
			automationContext.clearActiveTestCase();
		}
	}
	
	@Override
	public boolean init()
	{
		//dont consider data provider, for already data-based executors
		if(testCaseData != null)
		{
			return true;
		}
		
		IDataProvider dataProvider = testCase.getDataProvider();
		
		if(dataProvider == null)
		{
			return true;
		}
		
		if(!ExecutorUtils.executeSetup(dataSetup, "Data-Setup", this))
		{
			return false;
		}
		
		List<TestCaseData> dataLst = executeDataProvider(dataProvider);
		
		if(CollectionUtils.isEmpty(dataLst))
		{
			//testCaseBranch.result = new TestCaseResult(testCase, TestStatus.ERRORED, null, "No data from data-provider. Data Provider: " + dataProvider.getName(), startTime, new Date());
			throw new InvalidStateException("No data provided by data provider: {}", dataProvider.getName());
		}
		
		for(TestCaseData data : dataLst)
		{
			super.addChildExector(new TestCaseExecutor(testCase, data));
		}
		
		return true;
	}
	
	@Override
	protected void preCleanup()
	{
		ExecutorUtils.executeCleanup(dataCleanup, "Data-Cleanup", this);
	}

	@Override
	public void execute(Setup beforeChildFromParent, Cleanup afterChildFromParent)
	{
		AutomationContext.getInstance().setActiveTestCase(testCase, testCaseData);
		
		if(testCaseData != null)
		{
			AutomationContext.getInstance().setAttribute(testCase.getDataProvider().getName(), testCaseData.getValue());
			AutomationContext.getInstance().setAttribute(testCase.getDataProvider().getName() + ".name", testCaseData.getName());
		}
		
		try
		{
			super.execute(beforeChildFromParent, afterChildFromParent);
		} finally
		{
			AutomationContext.getInstance().clearActiveTestCase();
		}
	}
}