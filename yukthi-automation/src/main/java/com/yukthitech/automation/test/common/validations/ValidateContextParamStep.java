package com.yukthitech.automation.test.common.validations;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.AbstractValidation;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Validator to validate if specified value matches with specified context expression.
 * @author akiran
 */
@Executable(value = "validateContextParam", message = "Validates specified context param is present with specified value")
public class ValidateContextParamStep extends AbstractValidation implements Validateable
{
	/**
	 * Expression to be evaluated on context.
	 */
	private String expression;
	
	/**
	 * Value to be matched. Can be null, if null, only presence of param will be validated.
	 */
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
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(expression))
		{
			throw new ValidateException("Expression can not be null");
		}
	}

	@Override
	public boolean validate(AutomationContext context, IExecutionLogger exeLogger)
	{
		Object actualValue = null;
		
		try
		{
			actualValue = BeanUtils.getProperty(context, expression);
		}catch(Exception ex)
		{
			exeLogger.error(ex, "An error occurred while fetching property '{}' from context", expression);
			actualValue = null;
		}
		
		//if value is not found fail the validation
		if(actualValue == null)
		{
			return false;
		}
		
		//if value is not specified, since property is present return true
		if(value == null)
		{
			return true;
		}
		
		//match the value and return result
		return value.equals(actualValue.toString());
	}
}
