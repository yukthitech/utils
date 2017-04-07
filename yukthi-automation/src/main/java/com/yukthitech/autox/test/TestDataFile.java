package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a test data file used to configure test suites and other configurations.
 * @author akiran
 */
public class TestDataFile
{
	/**
	 * Test suites to be loaded.
	 */
	private List<TestSuite> testSuites = new ArrayList<>();
	
	/**
	 * Setup steps to be executed before executing any test suite.
	 */
	private Setup setup;
	
	/**
	 * Cleanup steps to be executed after executing all test suites.
	 */
	private Cleanup cleanup;

	/**
	 * Gets the setup steps to be executed before executing any test suite.
	 *
	 * @return the setup steps to be executed before executing any test suite
	 */
	public Setup getSetup()
	{
		return setup;
	}

	/**
	 * Sets the setup steps to be executed before executing any test suite.
	 *
	 * @param setup the new setup steps to be executed before executing any test suite
	 */
	public void setSetup(Setup setup)
	{
		if(this.setup != null)
		{
			throw new InvalidStateException("Multiple setup are specified under single test suite");
		}
		
		this.setup = setup;
	}

	/**
	 * Gets the cleanup steps to be executed after executing all test suites.
	 *
	 * @return the cleanup steps to be executed after executing all test suites
	 */
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Sets the cleanup steps to be executed after executing all test suites.
	 *
	 * @param cleanup the new cleanup steps to be executed after executing all test suites
	 */
	public void setCleanup(Cleanup cleanup)
	{
		if(this.cleanup != null)
		{
			throw new InvalidStateException("Multiple cleanup are specified under single test suite");
		}
		
		this.cleanup = cleanup;
	}

	/**
	 * Gets the test suites to be loaded.
	 *
	 * @return the test suites to be loaded
	 */
	public List<TestSuite> getTestSuites()
	{
		return testSuites;
	}

	/**
	 * Sets the test suites to be loaded.
	 *
	 * @param testSuites the new test suites to be loaded
	 */
	public void setTestSuites(List<TestSuite> testSuites)
	{
		if(testSuites == null)
		{
			throw new NullPointerException("Test suites can not be null.");
		}
		
		this.testSuites = testSuites;
	}

	/**
	 * Adds value to {@link #testSuites testSuites}
	 *
	 * @param testSuite
	 *            testSuite to be added
	 */
	public void addTestSuite(TestSuite testSuite)
	{
		testSuites.add(testSuite);
	}
}
