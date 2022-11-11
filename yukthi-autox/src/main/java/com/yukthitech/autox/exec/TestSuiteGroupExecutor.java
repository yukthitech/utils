package com.yukthitech.autox.exec;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.common.IAutomationConstants;
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
		
		testSuiteGroup
			.getTestSuites()
			.stream()
			.filter(ts -> limitedTestSuites == null || limitedTestSuites.contains(ts.getName()))
			.map(ts -> new TestSuiteExecutor(ts))
			.forEach(exec -> addChildExector(exec));
	}
}
