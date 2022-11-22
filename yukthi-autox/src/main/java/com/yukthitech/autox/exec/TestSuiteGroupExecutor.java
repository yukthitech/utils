package com.yukthitech.autox.exec;

import java.util.Set;

import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestSuiteGroup;

/**
 * Executor of test suite group.
 * @author akranthikiran
 */
public class TestSuiteGroupExecutor extends Executor
{
	public TestSuiteGroupExecutor(TestSuiteGroup testSuiteGroup)
	{
		super(testSuiteGroup, "Test-Suite");
		
		super.setup = testSuiteGroup.getSetup();
		super.cleanup = testSuiteGroup.getCleanup();
		
		AutomationContext context = AutomationContext.getInstance();
		String parallelExecutionEnabled = context.getOverridableProp(IAutomationConstants.AUTOX_PROP_PARALLEL_EXEC_ENABLED);
		super.parallelExecutionEnabled = "true".equalsIgnoreCase(parallelExecutionEnabled);
		
		BasicArguments basicArguments = context.getBasicArguments();
		Set<String> limitedTestSuites = basicArguments.getTestSuitesSet();
		Set<String> restrictedTestCases = basicArguments.getTestCasesSet();
		
		testSuiteGroup
			.getTestSuites()
			.stream()
			.filter(ts -> 
			{
				if(limitedTestSuites != null && !limitedTestSuites.contains(ts.getName()))
				{
					return false;
				}
				
				if(restrictedTestCases != null && !ts.hasAnyTestCases(restrictedTestCases))
				{
					return false;
				}
				
				return true;
			})
			.map(ts -> new TestSuiteExecutor(ts))
			.forEach(exec -> addChildExector(exec));
	}
	
	@Override
	public void execute(Setup beforeChildFromParent, Cleanup afterChildFromParent)
	{
		super.execute(beforeChildFromParent, afterChildFromParent);
		ReportDataManager.getInstance().generateReport();
	}
}
