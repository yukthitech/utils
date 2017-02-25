package com.yukthitech.automation.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.IStepContainer;
import com.yukthitech.automation.IValidation;
import com.yukthitech.automation.IValidationContainer;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Test case with validations to be executed.
 */
public class TestCase implements IStepContainer, IValidationContainer, Validateable
{
	/**
	 * Pattern used to replace expressions in step properties.
	 */
	private static Pattern CONTEXT_EXPR_PATTERN = Pattern.compile("\\{\\{(.+)\\}\\}");

	/**
	 * Name of the test case.
	 */
	private String name;

	/**
	 * Description about test case.
	 */
	private String description;

	/**
	 * Steps for the test case.
	 */
	private List<IStep> steps = new ArrayList<>();

	/**
	 * Validations of test case.
	 */
	private List<IValidation> validations = new ArrayList<>();

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
	 * Replaces expressions in specified step properties.
	 * @param context Context to fetch values for expressions.
	 * @param step Step in which expression has to be replaced
	 */
	private void replaceExpressions(AutomationContext context, IStep step)
	{
		Field fields[] = step.getClass().getDeclaredFields();
		String value = null;
		String propertyExpr = null;
		
		Matcher matcher = null;
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Object> contextAttr = context.getAttributeMap();
		
		for(Field field : fields)
		{
			//ignore non string fields
			if(!String.class.equals(field.getType()))
			{
				continue;
			}

			try
			{
				field.setAccessible(true);
				
				value = (String) field.get(step);
				
				//ignore null field values
				if(value == null)
				{
					continue;
				}
				
				matcher = CONTEXT_EXPR_PATTERN.matcher(value);
				buffer.setLength(0);
	
				//replace the expressions in the field value
				while(matcher.find())
				{
					propertyExpr = matcher.group(1);
					
					matcher.appendReplacement(buffer, BeanUtils.getProperty(contextAttr, propertyExpr));
				}
				
				matcher.appendTail(buffer);
				
				//set the result string back to field
				field.set(step, buffer.toString());
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while parsing expressions in field '{}' in class - {}", 
					field.getName(), step.getClass().getName());
			}
		}
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
		TestExecutionLogger exeLogger = new TestExecutionLogger();

		// execute the steps involved
		for(IStep step : steps)
		{
			exeLogger.debug("Executing step: {}", step);

			try
			{
				replaceExpressions(context, step);
				step.execute(context, exeLogger.getSubLogger());
			} catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing step - " + step);

				return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.toString(), "Step errored - " + step);
			}

			exeLogger.debug("Completed step: " + step);
		}

		// execute the validations
		for(IValidation validation : validations)
		{
			exeLogger.debug("Executing validation: {}", validation);

			try
			{
				if(!validation.validate(context, exeLogger.getSubLogger()))
				{
					exeLogger.error("Validation failed - " + validation);

					return new TestCaseResult(this.name, TestStatus.FAILED, exeLogger.toString(), validation.getFailureMessage());
				}
			} catch(Exception ex)
			{
				exeLogger.error(ex, "An error occurred while executing validation - " + validation);

				return new TestCaseResult(this.name, TestStatus.ERRORED, exeLogger.toString(), "Validation errored - " + validation);
			}

			exeLogger.debug("Completed validation: " + validation);
		}

		return new TestCaseResult(this.name, TestStatus.SUCCESSUFUL, exeLogger.toString(), null);
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

		if(CollectionUtils.isEmpty(validations))
		{
			throw new ValidateException("No validations provided for test case - " + name);
		}
	}
}
