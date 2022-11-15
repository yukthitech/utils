package com.yukthitech.autox.exec;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestSuiteGroup;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executor of test suite group.
 * @author akranthikiran
 */
public class TestSuiteGroupExecutor extends Executor
{
	public TestSuiteGroupExecutor(TestSuiteGroup testSuiteGroup)
	{
		super(testSuiteGroup);
		
		super.setup = testSuiteGroup.getSetup();
		super.cleanup = testSuiteGroup.getCleanup();
		
		AutomationContext context = AutomationContext.getInstance();
		String parallelExecutionCountStr = context.getOverridableProp(IAutomationConstants.AUTOX_PROP_PARALLEL_EXEC);
		int parallelExecutionCount = 0;
		
		if(StringUtils.isNotBlank(parallelExecutionCountStr))
		{
			try
			{
				parallelExecutionCount = Integer.parseInt(parallelExecutionCountStr);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Invalid value specified for parallel execution count config '{}'. Value specified: {}", 
						IAutomationConstants.AUTOX_PROP_PARALLEL_EXEC, parallelExecutionCountStr, ex);
			}
			
			super.parallelCount = parallelExecutionCount;
		}
		
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
