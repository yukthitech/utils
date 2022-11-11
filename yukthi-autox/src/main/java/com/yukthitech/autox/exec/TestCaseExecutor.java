package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.exec.report.ReportManager;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TestCaseExecutor extends Executor
{
	private TestCase testCase;
	
	private TestCaseData testCaseData;
	
	private List<TestCaseExecutor> dependencies;
	
	public TestCaseExecutor(TestCase testCase)
	{
		super(testCase);
		
		this.testCase = testCase;
		
		if(testCase.getDataProvider() != null)
		{
			super.setup = testCase.getDataSetup();
			super.cleanup = testCase.getDataCleanup();
			super.parallelCount = testCase.getParallelExecutionCount();
			super.expectedException = testCase.getExpectedException();
		}
		else
		{
			super.childSteps = testCase.getSteps();
		}
	}
	
	private TestCaseExecutor(TestCase testCase, TestCaseData testCaseData)
	{
		super(testCase);
		
		this.testCase = testCase;
		this.testCaseData = testCaseData;
		super.childSteps = testCase.getSteps();
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
				ReportManager.getInstance().executionSkipped(ExecutionType.MAIN, this, 
						String.format("Skipped as required dependency test-case '%s' is found with status: %s", 
								dep.testCase.getName(), dep.status));
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void init()
	{
		//dont consider data provider, for already data-based executors
		if(testCaseData != null)
		{
			return;
		}
		
		IDataProvider dataProvider = testCase.getDataProvider();
		
		if(dataProvider == null)
		{
			return;
		}
		
		List<TestCaseData> dataLst = null;
		AutomationContext automationContext = AutomationContext.getInstance();
		automationContext.setActiveTestCase(testCase, null);
		
		try
		{
			dataLst = dataProvider.getStepData();
		} finally
		{
			automationContext.clearActiveTestCase();
		}
		
		if(CollectionUtils.isEmpty(dataLst))
		{
			//testCaseBranch.result = new TestCaseResult(testCase, TestStatus.ERRORED, null, "No data from data-provider. Data Provider: " + dataProvider.getName(), startTime, new Date());
			throw new InvalidStateException("No data provided by data provider: {}", dataProvider.getName());
		}
		
		for(TestCaseData data : dataLst)
		{
			super.addChildExector(new TestCaseExecutor(testCase, data));
		}
	}
}
