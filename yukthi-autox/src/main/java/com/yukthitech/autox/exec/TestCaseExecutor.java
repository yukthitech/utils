package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ReportLogFile;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestStatus;

public class TestCaseExecutor extends Executor
{
	private TestCase testCase;
	
	private TestCaseData testCaseData;
	
	private List<TestCaseExecutor> dependencies;
	
	private Setup dataSetup;
	
	private Cleanup dataCleanup;
	
	/**
	 * Flag indicating if {@link #init()} method is executed successfully.
	 */
	private boolean initialized = false;
	
	public TestCaseExecutor(TestCase testCase)
	{
		super(testCase, "Test-Case");
		
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
		super(testCase, null);
		
		this.testCase = testCase;
		this.testCaseData = testCaseData;
		super.childSteps = testCase.getSteps();
		super.expectedException = testCase.getExpectedException();
	}
	
	public TestCase getTestCase()
	{
		return testCase;
	}
	
	public boolean isDataProviderType()
	{
		return (testCaseData == null && testCase.getDataProvider() != null);
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
			List<TestCaseData> data = dataProvider.getStepData();
			
			if(CollectionUtils.isEmpty(data))
			{
				executionLogger.error("Data provider resulted in null or empty data list");
			}
			
			return data;
		} finally
		{
			automationContext.setExecutionLogger(null);
			automationContext.clearActiveTestCase();
		}
	}
	
	@Override
	protected boolean init()
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
			super.setStatus(TestStatus.ERRORED, String.format("No data provided by data provider: %s", dataProvider.getName()));
			return false;
		}
		
		for(TestCaseData data : dataLst)
		{
			super.addChildExector(new TestCaseExecutor(testCase, data));
		}
		
		initialized = true;
		return true;
	}
	
	@Override
	protected void preCleanup()
	{
		//if data init is not done or failed, no need to do cleaup
		if(!initialized)
		{
			return;
		}
		
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
		
		AutomationContext.getInstance().startLogMonitoring();
		
		try
		{
			super.execute(beforeChildFromParent, afterChildFromParent);
		} finally
		{
			Map<String, ReportLogFile> monitorLogs = AutomationContext.getInstance().stopLogMonitoring(ReportDataManager.getInstance().isErrored(this));
			ReportDataManager.getInstance().setMonitoringLogs(this, monitorLogs);
			
			AutomationContext.getInstance().clearActiveTestCase();
		}
	}
}
