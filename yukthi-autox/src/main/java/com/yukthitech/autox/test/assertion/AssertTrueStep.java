package com.yukthitech.autox.test.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Asserts given value is either boolean true or string 'true'.
 * @author akiran
 */
@Executable(name = "assertTrue", message = "Asserts given value is either boolean true or string 'true'")
public class AssertTrueStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	@Param(description = "Value to be checked.", sourceType = SourceType.EXPRESSION)
	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(this, "Checking the value for true [value : {}]", value);
		boolean res = false;
		
		if(value instanceof Boolean)
		{
			res = (Boolean) value;
		}
		else
		{
			res = "true".equalsIgnoreCase("" + value);
		}
		
		exeLogger.debug(this, "Result is: {}", res);
		return res;
	}

}
