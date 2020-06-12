package com.yukthitech.autox.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
	 * Author names of this test suite.
	 */
	private String author;
	
	/**
	 * Description about the test suite.
	 */
	private String description;

	/**
	 * List of test cases to be executed in this test suite.
	 */
	private Map<String, TestCase> testCases = new LinkedHashMap<>();
	
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
	
	/**
	 * Attributes which are set at test suite level.
	 */
	private Map<String, Object> attributes = new HashMap<>();
	
	/**
	 * Setup to be executed after every test case.
	 */
	private Setup beforeTestCase;
	
	/**
	 * Cleanup to be executed after every test case.
	 */
	private Cleanup afterTestCase;
	
	public TestSuite()
	{}
	
	public TestSuite(String name)
	{
		this.name = name;
	}
	
	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		if(StringUtils.isNotBlank(author))
		{
			Set<String> authors = new TreeSet<>(Arrays.asList(author.trim().split("\\s*\\,\\s*")));
			this.author = authors.stream().collect(Collectors.joining(", "));
		}
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
				this.testCases.putAll(newTestSuite.testCases);
			}
			
			this.testCases.values().forEach(tc -> tc.setParentTestSuite(this));
		}
		
		if(StringUtils.isNotBlank(newTestSuite.author))
		{
			Set<String> authors = new TreeSet<>(Arrays.asList(newTestSuite.author.trim().split("\\s*\\,\\s*")));
			
			if(StringUtils.isNotBlank(author))
			{
				authors.addAll(Arrays.asList(author.trim().split("\\s*\\,\\s*")));
			}
			
			this.author = authors.stream().collect(Collectors.joining(", "));
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
	 * Fetches the test case with specified name.
	 * @param name
	 * @return
	 */
	public TestCase getTestCase(String name)
	{
		return testCases.get(name);
	}

	/**
	 * Gets the list of test cases to be executed in this test suite.
	 *
	 * @return the list of test cases to be executed in this test suite
	 */
	public List<TestCase> getTestCases()
	{
		return new ArrayList<>(testCases.values());
	}

	/**
	 * Adds value to {@link #testCases testCases}.
	 *
	 * @param testCase
	 *            testCase to be added
	 */
	public void addTestCase(TestCase testCase)
	{
		TestCase oldTestCase = this.testCases.get(testCase.getName());
		
		if(oldTestCase != null)
		{
			throw new InvalidArgumentException("Duplicate test case name encountered: " + testCase.getName());
		}
		
		testCases.put(testCase.getName(), testCase);
		testCase.setParentTestSuite(this);
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
	
	public void setAttribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}
	
	public Setup getBeforeTestCase()
	{
		return beforeTestCase;
	}

	public void setBeforeTestCase(Setup beforeTestCase)
	{
		this.beforeTestCase = beforeTestCase;
	}

	public Cleanup getAfterTestCase()
	{
		return afterTestCase;
	}

	public void setAfterTestCase(Cleanup afterTestCase)
	{
		this.afterTestCase = afterTestCase;
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
	}
	
	@Override
	public String toString()
	{
		return "[TS: " + name + "]";
	}
}
