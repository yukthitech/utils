package com.yukthitech.autox.test.common.validations;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;

/**
 * Validates specified context param is null.
 * @author akiran
 */
@Executable(name = "validateNullContextParam", message = "Validates specified context param is null")
public class ValidateNullContextParamStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be evaluated on context.
	 */
	@Param(description = "Expression to be evaluated on context")
	private String expression;
	
	/**
	 * Sets the expression to be evaluated on context.
	 *
	 * @param expression the new expression to be evaluated on context
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug(this, "Current validation is disabled. Skipping validation execution. Enabled: " + enabled);
			return true;
		}
		
		exeLogger.debug(this, "Validating context expression '{}' is null", expression);

		Object actualValue = null;
		
		try
		{
			actualValue = PropertyUtils.getProperty(context, expression);
		}catch(Exception ex)
		{
			exeLogger.error(this, ex, "An error occurred while fetching property '{}' from context. So assuming value as null.", expression);
			actualValue = null;
			return true;
		}
		
		//if value is not found fail the validation
		if(actualValue == null)
		{
			exeLogger.debug(this, "Context expression '{}' is evaluated as null", expression);
			return true;
		}
		
		exeLogger.debug(this, "Context expression '{}' is evaluated to non-null value - {}", expression, actualValue);
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("expression: ").append(expression);

		builder.append("]");
		return builder.toString();
	}

}
