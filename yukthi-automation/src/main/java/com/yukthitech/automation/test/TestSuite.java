package com.yukthitech.automation.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

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
	 * Test data beans that can be used by test cases.
	 */
	private Map<String, Object> dataBeans = new HashMap<>();
	
	/**
	 * Dependency test suites. If specified, framework will ensure the specified test suites will be executed
	 * before this test suite.
	 */
	private List<String> dependencies;
	
	/**
	 * Status of the test suite.
	 */
	private TestSuiteStatus status;
	
	/**
	 * Status message.
	 */
	private String statusMessage;

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
	 * Gets the test data beans that can be used by test cases.
	 *
	 * @return the test data beans that can be used by test cases
	 */
	public Map<String, Object> getDataBeans()
	{
		return dataBeans;
	}

	/**
	 * Sets the test data beans that can be used by test cases.
	 *
	 * @param dataBeans the new test data beans that can be used by test cases
	 */
	public void setDataBeans(Map<String, Object> dataBeans)
	{
		if(dataBeans == null)
		{
			throw new NullPointerException("Data beans can not be null");
		}
		
		this.dataBeans = dataBeans;
	}
	
	/**
	 * Adds specified data bean to this test suite.
	 * @param name Name of the data bean.
	 * @param bean Bean to be added.
	 */
	public void addDataBean(String name, Object bean)
	{
		dataBeans.put(name, bean);
	}

	/**
	 * Gets data bean with specified name.
	 * @param name Name of the data bean
	 * @return matching data bean
	 */
	public Object getDataBean(String name)
	{
		return dataBeans.get(name);
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
	 * Gets the status of the test suite.
	 *
	 * @return the status of the test suite
	 */
	public TestSuiteStatus getStatus()
	{
		return status;
	}

	/**
	 * Sets the status of the test suite.
	 *
	 * @param status the new status of the test suite
	 */
	public void setStatus(TestSuiteStatus status)
	{
		this.status = status;
	}

	/**
	 * Gets the status message.
	 *
	 * @return the status message
	 */
	public String getStatusMessage()
	{
		return statusMessage;
	}

	/**
	 * Sets the status message.
	 *
	 * @param statusMessage the new status message
	 */
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
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
