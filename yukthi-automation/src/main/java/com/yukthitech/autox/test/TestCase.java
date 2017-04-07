package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.IValidationContainer;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Test case with validations to be executed.
 */
public class TestCase implements IStepContainer, IValidationContainer, Validateable
{
	private static Logger logger = LogManager.getLogger(TestCase.class);
	
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
	 * Validations of test case.
	 */
	private List<IValidation> validations = new ArrayList<>();
	
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
		if(steps == null)
		{
			steps = new ArrayList<IStep>();
		}

		steps.add(step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidationContainer#addValidation(com.yukthitech.ui
	 * .automation.IValidation)
	 */
	@Override
	public void addValidation(IValidation validation)
	{
		if(validations == null)
		{
			validations = new ArrayList<IValidation>();
		}

		validations.add(validation);
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
			//clone the step, so that expression replacement will not affect actual step
			step = step.clone();
			
			try
			{
				AutomationUtils.replaceExpressions(context, step);
				step.execute(context, exeLogger);
			} catch(Exception ex)
			{
				Executable executable = step.getClass().getAnnotation(Executable.class);
				
				if(expectedException != null)
				{
					try
					{
						expectedException.validateMatch(ex);
						expectedExcpetionOccurred = true;
						
						exeLogger.debug("Expected excpetion occurred: {}", ex);
						break;
					}catch(InvalidArgumentException iex)
					{
						exeLogger.error(ex, ex.getMessage());
						return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Step errored: " + executable.name());
					}
				}
				
				//return error only if the exception was not expected one
				exeLogger.error(ex, "An error occurred while executing step: " + executable.name());
				return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Step errored: " + executable.name());
			}
		}
		
		if(expectedException != null && !expectedExcpetionOccurred)
		{
			return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Expected exception '" + expectedException.getType() + "' did not occur.");
		}

		// execute the validations
		for(IValidation validation : validations)
		{
			validation = validation.clone();
			
			try
			{
				AutomationUtils.replaceExpressions(context, validation);
				
				if(!validation.validate(context, exeLogger))
				{
					Executable executable = validation.getClass().getAnnotation(Executable.class);
					exeLogger.error("Validation failed: " + executable.name() + validation);

					return new TestCaseResult(name, TestStatus.FAILED, exeLogger.getExecutionLogData(), "Validation failed: " + executable.name() + validation);
				}
			} catch(TestCaseFailedException ex)
			{
				Executable executable = validation.getClass().getAnnotation(Executable.class);
				
				//for handled exceptions dont log on ui
				logger.error("An error occurred while executing validation: " + executable.name(), ex);
				
				return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + executable.name());
			} catch(Exception ex)
			{
				Executable executable = validation.getClass().getAnnotation(Executable.class);
				
				//for unhandled exceptions dont log on ui
				exeLogger.error(ex, "An error occurred while executing validation: " + executable.name());
				
				return new TestCaseResult(name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + executable.name());
			}
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

		if(CollectionUtils.isEmpty(validations) && expectedException == null)
		{
			throw new ValidateException("No validations provided for test case - " + name);
		}
	}
}
