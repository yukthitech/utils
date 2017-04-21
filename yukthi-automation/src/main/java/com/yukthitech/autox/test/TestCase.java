package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Test case with validations to be executed.
 */
public class TestCase implements IStepContainer, Validateable
{
	/**
	 * Name of the test case.
	 */
	private String name;

	/**
	 * Description about test case.
	 */
	private String description;
	
	/**
	 * Dependency test cases within the current test suite. Dependencies are considered valid only if they occur
	 * in the same test suite and occurs before current test suite. If a valid dependency test case
	 * was not executed successfully then current test case will be skipped.
	 */
	private String dependencies;

	/**
	 * Steps for the test case.
	 */
	private List<IStep> steps = new ArrayList<>();

	/**
	 * Details of the exception expected from this test case.
	 */
	private ExpectedException expectedException;
	
	/**
	 * Data provider to be used for this test case.
	 */
	private IDataProvider dataProvider;

	/**
	 * Gets the name of the test case.
	 *
	 * @return the name of the test case
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the test case.
	 *
	 * @param name
	 *            the new name of the test case
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the dependency test cases within the current test suite. Dependencies are considered valid only if they occur in the same test suite and occurs before current test suite. If a valid dependency test case was not executed successfully then current test case will be skipped.
	 *
	 * @return the dependency test cases within the current test suite
	 */
	public String getDependencies()
	{
		return dependencies;
	}

	/**
	 * Sets the dependency test cases within the current test suite. Dependencies are considered valid only if they occur in the same test suite and occurs before current test suite. If a valid dependency test case was not executed successfully then current test case will be skipped.
	 *
	 * @param dependencies the new dependency test cases within the current test suite
	 */
	public void setDependencies(String dependencies)
	{
		this.dependencies = dependencies;
	}
	
	/**
	 * Sets the data provider to be used for this test case.
	 *
	 * @param dataProvider the new data provider to be used for this test case
	 */
	public void setDataProvider(IDataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}
	
	/**
	 * Gets the data provider to be used for this test case.
	 *
	 * @return the data provider to be used for this test case
	 */
	public IDataProvider getDataProvider()
	{
		return dataProvider;
	}
	
	/**
	 * Sets the specified list data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setListDataProvider(ListDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
	
	/**
	 * Sets the specified range data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setRangeDataProvider(RangeDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
	
	/**
	 * Fetches dependencies as a set.
	 * @return dependencies set
	 */
	public Set<String> getDependenciesSet()
	{
		if(StringUtils.isBlank(dependencies))
		{
			return null;
		}
		
		String depArr[] = dependencies.trim().split("\\s*\\,\\s*");
		return new HashSet<>(Arrays.asList(depArr));
	}

	/**
	 * Gets the description about test case.
	 *
	 * @return the description about test case
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about test case.
	 *
	 * @param description
	 *            the new description about test case
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IStepContainer#addStep(com.yukthitech.ui.automation.
	 * IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}

	/**
	 * Sets the details of the exception expected from this test case.
	 *
	 * @param expectedException the new details of the exception expected from this test case
	 */
	public void setExpectedException(ExpectedException expectedException)
	{
		this.expectedException = expectedException;
	}
	
	/**
	 * Called internally multiple times per data object provided by data provider.
	 * @param context
	 * @return
	 */
	public TestCaseResult execute(AutomationContext context, TestCaseData testCaseData, ExecutionLogger exeLogger)
	{
		String name = this.name;
		
		if(testCaseData != null)
		{
			name += "[" + testCaseData.getName() + "]";
		}
		
		ExpectedException expectedException = null;
		
		if(this.expectedException != null)
		{
			expectedException = this.expectedException.clone();
			AutomationUtils.replaceExpressions(context, expectedException);
			
			//if expected exception is disabled, ignore the expected exception
			if(!"true".equals(expectedException.getEnabled()))
			{
				expectedException = null;
			}
		}
		
		boolean expectedExcpetionOccurred = false;
		
		// execute the steps involved
		for(IStep step : steps)
		{
			try
			{
				StepExecutor.executeStep(context, exeLogger, step);
			} catch(Exception ex)
			{
				TestCaseResult result = StepExecutor.handleException(step, exeLogger, ex, expectedException);
				
				if(result != null)
				{
					return result;
				}
				
				expectedExcpetionOccurred = true;
				break;
			}
		}
		
		if(expectedException != null && !expectedExcpetionOccurred)
		{
			return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Expected exception '" + expectedException.getType() + "' did not occur.");
		}

		return new TestCaseResult(name, TestStatus.SUCCESSFUL, exeLogger.getExecutionLogData(), null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.core.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isEmpty(name))
		{
			throw new ValidateException("No name is provided for test case.");
		}

		if(StringUtils.isEmpty(description))
		{
			throw new ValidateException("No description is provided for test case - " + name);
		}
		
		if(CollectionUtils.isEmpty(steps))
		{
			throw new ValidateException("No steps specified for execution of test case - " + name);
		}
		
		IStep lastStep = steps.get(steps.size() - 1);

		if( !(lastStep instanceof IValidation) && expectedException == null)
		{
			throw new ValidateException("No validations provided at end of test case - " + name);
		}
	}
}
