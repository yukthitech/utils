package com.yukthitech.autox.test.common.validations;

import org.apache.commons.jxpath.JXPathContext;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.IAutomationConstants;

/**
 * Validator to validate if specified value matches with specified context expression.
 * @author akiran
 */
@Executable(name = "validateXpathValue", message = "Validates specified xpath value is present with specified value")
public class ValidateXpathValueStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Jx value expression, which should be executed on source to get the value.")
	private String valueExpression;

	/** The source. */
	@Param(description = "Source on which jx expression has to be evaluated. Source can be a bean or json string or a file resource (file:resource-path)")
	private Object source;
	
	/**
	 * Value to be matched. Can be null, if null, only presence of param will be validated.
	 */
	@Param(description = "Value to be matched. If not specifed, only presence of param will be validated.", required = false)
	private String value;

	/**
	 * Sets the value of the attribute to set.
	 *
	 * @param valueExpression the new value of the attribute to set
	 */
	public void setValueExpression(String valueExpression)
	{
		this.valueExpression = valueExpression;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(Object source)
	{
		this.source = source;
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
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution. Enabled: " + enabled);
			return true;
		}
		
		if(value == null)
		{
			exeLogger.debug("Validating xpath expression '{}' is present", valueExpression);
		}
		else
		{
			exeLogger.debug("Validating context expression '{}' is: {}", valueExpression, value);
		}
		
		Object sourceValue = IAutomationConstants.PARSE_OBJ_SOURCE(context, exeLogger, source);

		Object actualValue = JXPathContext.newContext(sourceValue).getValue(valueExpression);
		
		//if value is not found fail the validation
		if(actualValue == null)
		{
			exeLogger.debug("Value expression '{}' is evaluated as null", valueExpression);
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
			exeLogger.error("Validation failed, found expected value and actual value are different. "
					+ "\n[Actual Value: {}, Expected Value: {}]", actualValue, value);
		}
		
		return res;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Expression: ").append(valueExpression);
		builder.append(", ").append("value: ").append(value);

		builder.append("]");
		return builder.toString();
	}

}
