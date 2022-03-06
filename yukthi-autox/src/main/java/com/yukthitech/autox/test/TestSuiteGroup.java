package com.yukthitech.autox.test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.BasicArguments;
import com.yukthitech.autox.exec.ExecutionBranch;
import com.yukthitech.autox.exec.ExecutionBranchBuilder;
import com.yukthitech.autox.exec.IExecutable;

/**
 * The Class TestSuiteGroup.
 */
public class TestSuiteGroup implements IExecutable
{
	private static Logger logger = LogManager.getLogger(TestSuiteGroup.class);
	
	/**
	 * Setup suites to be executed before any test suite.
	 */
	private Setup setup;

	/**
	 * Cleanup steps to be executed before all test suites.
	 */
	private Cleanup cleanup;

	/**
	 * Test suites to be executed. By default ordered by name.
	 */
	private Map<String, TestSuite> testSuitesMap = new TreeMap<>();

	/**
	 * Gets the setup suites to be executed before any test suite.
	 *
	 * @return the setup suites to be executed before any test suite
	 */
	public Setup getSetup()
	{
		return setup;
	}
	
	/**
	 * Sets the setup suites to be executed before any test suite.
	 *
	 * @param setup the new setup suites to be executed before any test suite
	 */
	public void setSetup(Setup setup)
	{
		this.setup = setup;
	}

	/**
	 * Gets the cleanup steps to be executed before all test suites.
	 *
	 * @return the cleanup steps to be executed before all test suites
	 */
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Sets the cleanup steps to be executed before all test suites.
	 *
	 * @param cleanup the new cleanup steps to be executed before all test suites
	 */
	public void setCleanup(Cleanup cleanup)
	{
		this.cleanup = cleanup;
	}

	/**
	 * Returns test suites of this group.
	 * @return test suites
	 */
	public Collection<TestSuite> getTestSuites()
	{
		return Collections.unmodifiableCollection( testSuitesMap.values() );
	}
	
	/**
	 * Adds the specified test suite.
	 * @param testSuite test suite to add.
	 */
	public void addTestSuite(TestSuite testSuite)
	{
		TestSuite existingTestSuite = this.testSuitesMap.get(testSuite.getName());
		
		if(existingTestSuite == null)
		{
			this.testSuitesMap.put(testSuite.getName(), testSuite);
		}
		else
		{
			existingTestSuite.merge(testSuite);
		}
	}
	
	/**
	 * Checks if the specified name is valid test suite name.
	 * @param name name to check
	 * @return true if valid
	 */
	public boolean isValidTestSuiteName(String name)
	{
		return this.testSuitesMap.containsKey(name);
	}
	
	/**
	 * Returns test suite with specified name.
	 * @param name Name of test suite to fetch
	 * @return Matching test suite.
	 */
	public TestSuite getTestSuite(String name)
	{
		return testSuitesMap.get(name);
	}

	@Override
	public ExecutionBranch buildExecutionBranch(AutomationContext context)
	{
		BasicArguments basicArguments = context.getBasicArguments();
		Set<String> limitedTestSuites = basicArguments.getTestSuitesSet();
		
		if(limitedTestSuites != null)
		{
			logger.debug("Limiting test-suite executions to: {}", limitedTestSuites);
		}

		return ExecutionBranchBuilder
				.newBranchBuilder(context, "_global", "Global", this, this.testSuitesMap.values())
				.setup(setup)
				.cleanup(cleanup)
				.childFilter(ts -> limitedTestSuites == null || limitedTestSuites.contains(ts.getName()))
				.build();
	}
}
