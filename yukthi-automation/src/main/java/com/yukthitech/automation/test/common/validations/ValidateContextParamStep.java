package com.yukthitech.automation.test.common.validations;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.IValidation;
import com.yukthitech.automation.Param;

/**
 * Validator to validate if specified value matches with specified context expression.
 * @author akiran
 */
@Executable(name = "validateContextParam", message = "Validates specified context param is present with specified value")
public class ValidateContextParamStep implements IValidation
{
	/**
	 * Expression to be evaluated on context.
	 */
	@Param(description = "Expression to be evaluated on context")
	private String expression;
	
	/**
	 * Value to be matched. Can be null, if null, only presence of param will be validated.
	 */
	@Param(description = "Value to be matched. If not specifed, only presence of param will be validated.", required = false)
	private String value;

	/**
	 * Sets the expression to be evaluated on context.
	 *
	 * @param expression the new expression to be evaluated on context
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Sets the value to be matched. Can be null, if null, only presence of param will be validated.
	 *
	 * @param value the new value to be matched
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public boolean validate(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(value == null)
		{
			exeLogger.debug("Validating context expression '{}' is present", expression);
		}
		else
		{
			exeLogger.debug("Validating context expression '{}' is: {}", expression, value);
		}
		
		Object actualValue = null;
		
		try
		{
			actualValue = BeanUtils.getProperty(context, expression);
		}catch(Exception ex)
		{
			exeLogger.error(ex, "An error occurred while fetching property '{}' from context", expression);
			actualValue = null;
			return false;
		}
		
		//if value is not found fail the validation
		if(actualValue == null)
		{
			exeLogger.debug("Context expression '{}' is evaluated as null", expression);
			return false;
		}
		
		//if value is not specified, since property is present return true
		if(value == null)
		{
			return true;
		}
		
		//match the value and return result
		boolean res = value.equals(actualValue.toString());
		
		if(res)
		{
			exeLogger.debug("Validation was successful");
		}
		else
		{
			exeLogger.debug("Validation failed, found expected value and context value are different. "
					+ "\n[Value from context: {}, Expected Value: {}]", actualValue, value);
		}
		
		return res;
	}
}
