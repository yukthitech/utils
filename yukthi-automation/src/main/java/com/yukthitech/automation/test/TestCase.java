package com.yukthitech.automation.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.IStepContainer;
import com.yukthitech.automation.IValidation;
import com.yukthitech.automation.IValidationContainer;
import com.yukthitech.automation.common.AutomationUtils;
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
	private ExceptionExpected expectedException;

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
	public void setExpectedException(ExceptionExpected expectedException)
	{
		this.expectedException = expectedException;
	}

	/**
	 * Execute.
	 *
	 * @param context
	 *            the context
	 * @return the test case result
	 */
	public TestCaseResult execute(AutomationContext context)
	{
		ExecutionLogger exeLogger = new ExecutionLogger(name, description);
		boolean expectedExcpetionOccurred = (expectedException == null);
		
		// execute the steps involved
		for(IStep step : steps)
		{
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
					}catch(InvalidArgumentException iex)
					{
						exeLogger.error(ex, ex.getMessage());
						return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Step errored: " + executable.name());
					}
				}
				
				//return error only if the exception was not expected one
				if(!expectedExcpetionOccurred)
				{
					exeLogger.error(ex, "An error occurred while executing step: " + executable.name());
					return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Step errored: " + executable.name());
				}
			}
		}
		
		if(!expectedExcpetionOccurred)
		{
			return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Expected exception '" + expectedException.getType().getName() + "' did not occur.");
		}

		// execute the validations
		for(IValidation validation : validations)
		{
			try
			{
				AutomationUtils.replaceExpressions(context, validation);
				
				if(!validation.validate(context, exeLogger))
				{
					Executable executable = validation.getClass().getAnnotation(Executable.class);
					exeLogger.error("Validation failed: " + executable.name() + validation);

					return new TestCaseResult(this.name, TestStatus.FAILED, exeLogger.getExecutionLogData(), "Validation failed: " + executable.name() + validation);
				}
			} catch(TestCaseFailedException ex)
			{
				Executable executable = validation.getClass().getAnnotation(Executable.class);
				
				//for handled exceptions dont log on ui
				logger.error("An error occurred while executing validation: " + executable.name(), ex);
				
				return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + executable.name());
			} catch(Exception ex)
			{
				Executable executable = validation.getClass().getAnnotation(Executable.class);
				
				//for unhandled exceptions dont log on ui
				exeLogger.error(ex, "An error occurred while executing validation: " + executable.name());
				
				return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.getExecutionLogData(), "Validation errored: " + executable.name());
			}
		}

		return new TestCaseResult(this.name, TestStatus.SUCCESSFUL, exeLogger.getExecutionLogData(), null);
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
