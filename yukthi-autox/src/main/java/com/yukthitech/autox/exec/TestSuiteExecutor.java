package com.yukthitech.autox.exec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestSuite;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executor for test suites.
 * @author akranthikiran
 */
public class TestSuiteExecutor extends BaseExecutor
{
	private static Logger logger = LogManager.getLogger(TestSuiteExecutor.class);
	
	private TestSuite testSuite;
	
	public TestSuiteExecutor(AutomationContext context, TestSuite testSuite)
	{
		super(context, "ts", testSuite.getName(), testSuite.getDescription());
		
		this.testSuite = testSuite;
		super.setup = testSuite.getSetup();
		super.cleanup = testSuite.getCleanup();
		super.beforeChild = testSuite.getBeforeTestCase();
		super.afterChild = testSuite.getAfterTestCase();
		super.parallelCount = testSuite.getParallelExecutionCount();
		
		//fetch restricted test-cases along with dependencies
		Set<String> restrictedTestCases = getRestrictedTestCases();
		
		//add test case executors in order of dependencies
		List<TestCase> orderedTestCases = testSuite.fetchOrderedTestCases();
		Map<String, TestCaseExecutor> executorMap = new HashMap<>();
		
		ObjectWrapper<String> excludedGroup = new ObjectWrapper<>();

		//create executor and child dependencies
		for(TestCase testCase : orderedTestCases)
		{
			if(restrictedTestCases != null && !restrictedTestCases.contains(testCase.getName()))
			{
				logger.debug("Exluding test-case '{}' as it is NOT part of restricted test cases", testCase.getName());
				continue;
			}
			
			if(testCase.isExecutable(context, excludedGroup))
			{
				logger.debug("Exluding test-case '{}' as it is part of excluded group: {}", testCase.getName(), excludedGroup.getValue());
				continue;
			}
			
			TestCaseExecutor executor = new TestCaseExecutor(context, testCase);
			executorMap.put(testCase.getName(), executor);
			super.addChildExector(executor);
			
			Set<String> depLst = testCase.getDependenciesSet();
			
			if(CollectionUtils.isEmpty(depLst))
			{
				continue;
			}
			
			for(String dep : depLst)
			{
				TestCaseExecutor depExecutor = executorMap.get(dep);
				
				if(depExecutor == null)
				{
					throw new InvalidStateException("For test-case '{}' required dependency test-case '{}' is not found or excluded", testCase.getName(), dep);
				}
				
				executor.addDependency(depExecutor);
			}
		}
	}
	
	private Set<String> getRestrictedTestCases()
	{
		Set<String> restrictedTestCases = automationContext.getBasicArguments().getTestCasesSet();
		
		if(CollectionUtils.isEmpty(restrictedTestCases))
		{
			return null;
		}
		
		Set<String> tcWithDep = new HashSet<>();
		
		for(String tc : restrictedTestCases)
		{
			TestCase testCase = testSuite.getTestCase(tc);
			
			if(testCase == null)
			{
				throw new InvalidStateException("Invalid restricted test case specified: {}", tc);
			}
			
			tcWithDep.add(tc);
			
			Set<String> depLst = testCase.getDependenciesSet();
			
			if(CollectionUtils.isNotEmpty(depLst))
			{
				tcWithDep.addAll(depLst);
			}
		}
		
		return tcWithDep;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(testSuite.getName());

		builder.append("]");
		return builder.toString();
	}

	
}
