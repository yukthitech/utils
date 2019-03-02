package com.yukthitech.autox.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
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
	private List<Setup> setups;
	
	/**
	 * Cleanup steps to be executed after executing test suite.
	 */
	private List<Cleanup> cleanups;
	
	/**
	 * Name to step group mapping.
	 */
	private Map<String, Function> nameToFunction = new HashMap<>();
	
	/**
	 * File in which this test suite is defined.
	 */
	private File file;
	
	public TestSuite()
	{}
	
	public TestSuite(String name)
	{
		this.name = name;
	}
	
	/**
	 * Sets the file in which this test suite is defined.
	 *
	 * @param file the new file in which this test suite is defined
	 */
	public void setFile(File file)
	{
		try
		{
			this.file = file.getCanonicalFile();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while determining cannotical path", ex);
		}
	}
	
	/**
	 * Gets the file in which this test suite is defined.
	 *
	 * @return the file in which this test suite is defined
	 */
	public File getFile()
	{
		return file;
	}
	
	public void merge(TestSuite newTestSuite)
	{
		if(newTestSuite.getSetups() != null)
		{
			if(this.setups == null)
			{
				this.setups = newTestSuite.setups;
			}
			else
			{
				this.setups.addAll(newTestSuite.setups);
			}
		}
		
		if(newTestSuite.cleanups != null)
		{
			if(this.cleanups == null)
			{
				this.cleanups = newTestSuite.cleanups;
			}
			else
			{
				this.cleanups.addAll(newTestSuite.cleanups);
			}
		}
		
		if(newTestSuite.testCases != null)
		{
			if(this.testCases == null)
			{
				this.testCases = newTestSuite.testCases;
			}
			else
			{
				this.testCases.addAll(newTestSuite.testCases);
			}
		}
		
		this.nameToFunction.putAll(newTestSuite.nameToFunction);
	}

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
	public List<Setup> getSetups()
	{
		return setups;
	}

	/**
	 * Sets the setup steps to be executed before executing test suite.
	 *
	 * @param setup the new setup steps to be executed before executing test suite
	 */
	public void addSetup(Setup setup)
	{
		if(this.setups == null)
		{
			this.setups = new ArrayList<>();
		}
		
		this.setups.add(setup);
	}

	/**
	 * Gets the cleanup steps to be executed after executing test suite.
	 *
	 * @return the cleanup steps to be executed after executing test suite
	 */
	public List<Cleanup> getCleanups()
	{
		return cleanups;
	}

	/**
	 * Sets the cleanup steps to be executed after executing test suite.
	 *
	 * @param cleanup the new cleanup steps to be executed after executing test suite
	 */
	public void addCleanup(Cleanup cleanup)
	{
		if(this.cleanups == null)
		{
			this.cleanups = new ArrayList<>();
		}
		
		this.cleanups.add(cleanup);
	}

	/**
	 * Adds specified test group.
	 * @param function group to add.
	 */
	public void addFunction(Function function)
	{
		if(StringUtils.isEmpty(function.getName()))
		{
			throw new InvalidArgumentException("Step group can not be added without name");
		}
		
		if(nameToFunction.containsKey(function.getName()))
		{
			throw new InvalidStateException("Duplicate step group name encountered: {}", function.getName());
		}
		
		function.markAsFunctionGroup();
		nameToFunction.put(function.getName(), function);
	}

	
	/**
	 * Fetches the step group with specified name.
	 * @param name name of step group.
	 * @return matching group
	 */
	public Function getFunction(String name)
	{
		return nameToFunction.get(name);
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
