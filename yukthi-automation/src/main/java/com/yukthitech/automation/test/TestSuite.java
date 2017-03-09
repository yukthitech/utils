package com.yukthitech.automation.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yukthitech.automation.config.ApplicationConfiguration;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a group of test cases to be executed.
 * 
 * @author akiran
 */
public class TestSuite implements Validateable
{
	/**
	 * Name of the test suite.
	 */
	private String name;
	
	/**
	 * Description about the test suite.
	 */
	private String description;

	/**
	 * List of test cases to be executed in this test suite.
	 */
	private List<TestCase> testCases = new ArrayList<>();
	
	/**
	 * Dependency test suites. If specified, framework will ensure the specified test suites will be executed
	 * before this test suite.
	 */
	private List<String> dependencies;
	
	/**
	 * Setup steps to be executed before executing test suite.
	 */
	private Setup setup;
	
	/**
	 * Cleanup steps to be executed after executing test suite.
	 */
	private Cleanup cleanup;

	/**
	 * Gets the name of the test suite.
	 *
	 * @return the name of the test suite
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the test suite.
	 *
	 * @param name
	 *            the new name of the test suite
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the description about the test suite.
	 *
	 * @return the description about the test suite
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the test suite.
	 *
	 * @param description the new description about the test suite
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the list of test cases to be executed in this test suite.
	 *
	 * @return the list of test cases to be executed in this test suite
	 */
	public List<TestCase> getTestCases()
	{
		return testCases;
	}

	/**
	 * Sets the list of test cases to be executed in this test suite.
	 *
	 * @param testCases
	 *            the new list of test cases to be executed in this test suite
	 */
	public void setTestCases(List<TestCase> testCases)
	{
		if(testCases == null)
		{
			throw new NullPointerException("Test cases can not be null.");
		}
		
		this.testCases = testCases;
	}

	/**
	 * Adds value to {@link #testCases testCases}.
	 *
	 * @param testCase
	 *            testCase to be added
	 */
	public void addTestCase(TestCase testCase)
	{
		testCases.add(testCase);
	}

	/**
	 * Adds specified data bean to this application.
	 * @param name Name of the data bean.
	 * @param bean Bean to be added.
	 */
	public void addDataBean(String name, Object bean)
	{
		ApplicationConfiguration.getInstance().addDataBean(name, bean);
	}

	/**
	 * Gets the dependency test suites. If specified, framework will ensure the specified test suites will be executed before this test suite.
	 *
	 * @return the dependency test suites
	 */
	public List<String> getDependencies()
	{
		return dependencies;
	}

	/**
	 * Sets the dependency test suites. If specified, framework will ensure the specified test suites will be executed before this test suite.
	 *
	 * @param dependencies the new dependency test suites
	 */
	public void setDependencies(List<String> dependencies)
	{
		this.dependencies = dependencies;
	}
	
	/**
	 * Specifies the dependency test suite names as comma separated string.
	 * @param dependencies Dependency list as comma separated string.
	 */
	public void setDependencyList(String dependencies)
	{
		String depLst[] = dependencies.split("\\s*\\,\\s*");
		this.dependencies = new ArrayList<>(Arrays.asList(depLst));
	}

	/**
	 * Gets the setup steps to be executed before executing test suite.
	 *
	 * @return the setup steps to be executed before executing test suite
	 */
	public Setup getSetup()
	{
		return setup;
	}

	/**
	 * Sets the setup steps to be executed before executing test suite.
	 *
	 * @param setup the new setup steps to be executed before executing test suite
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
	 * Gets the cleanup steps to be executed after executing test suite.
	 *
	 * @return the cleanup steps to be executed after executing test suite
	 */
	public Cleanup getCleanup()
	{
		return cleanup;
	}

	/**
	 * Sets the cleanup steps to be executed after executing test suite.
	 *
	 * @param cleanup the new cleanup steps to be executed after executing test suite
	 */
	public void setCleanup(Cleanup cleanup)
	{
		if(this.cleanup != null)
		{
			throw new InvalidStateException("Multiple cleanup are specified under single test suite");
		}
		
		this.cleanup = cleanup;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new ValidateException("No name is provided for test suite.");
		}

		if(testCases.isEmpty())
		{
			throw new ValidateException("No test cases specified under this test suite.");
		}
	}
}
