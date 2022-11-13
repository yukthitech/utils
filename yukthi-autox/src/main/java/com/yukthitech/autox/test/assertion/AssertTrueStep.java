package com.yukthitech.autox.test.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Asserts given value is either boolean true or string 'true'.
 * @author akiran
 */
@Executable(name = "assertTrue", group = Group.Common, message = "Asserts given value is either boolean true or string 'true'")
public class AssertTrueStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	@Param(description = "Value to be checked.", sourceType = SourceType.EXPRESSION)
	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking the value for true [value : {}]", value);
		boolean isTrue = false;
		
		if(value instanceof Boolean)
		{
			isTrue = (Boolean) value;
		}
		else
		{
			isTrue = "true".equalsIgnoreCase(("" + value).trim());
		}
		
		exeLogger.debug("Found value to be: {}", isTrue);

		if(!isTrue)
		{
			AssertTrueStep actualStep = (AssertTrueStep) super.sourceStep;
			throw new AutoxValidationException(this, "Expression evaluated to be false: {}", 
					actualStep.value);
		}
	}

}
