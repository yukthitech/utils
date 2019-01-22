package com.yukthitech.autox.test.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Asserts given value is either boolean false or string 'false'.
 * @author akiran
 */
@Executable(name = "assertFalse", message = "Asserts given value is either boolean false or string 'false'")
public class AssertFalseStep extends AbstractValidation
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Value to be evaluated.
	 */
	@Param(description = "Value to be evaluated", sourceType = SourceType.EXPRESSION)
	private Object value;

	/**
	 * Sets the value to be evaluated.
	 *
	 * @param value the new value to be evaluated
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(this, "Checking the value is false [value : {}]", value);
		boolean res = false;

		if(value instanceof Boolean)
		{
			res = (Boolean) value;
		}
		else
		{
			res = !"true".equalsIgnoreCase("" + value);
		}

		exeLogger.debug(this, "Result is: {}", res);
		return !res;

	}

}
