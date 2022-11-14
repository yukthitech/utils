package com.yukthitech.autox.test.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Asserts given value is either boolean false or string 'false'.
 * @author akiran
 */
@Executable(name = "assertFalse", group = Group.Common, message = "Asserts given value is either boolean false or string 'false'")
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
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking the value is false [value : {}]", value);
		boolean isTrue = false;

		if(value instanceof Boolean)
		{
			isTrue = (Boolean) value;
		}
		else
		{
			isTrue = "true".equalsIgnoreCase(("" + value).trim());
		}

		exeLogger.debug("Result is: {}", isTrue);
		
		if(isTrue)
		{
			AssertFalseStep actualStep = (AssertFalseStep) super.sourceStep;
			throw new AutoxValidationException(this, "Expression evaluated to be true: {}", 
					actualStep.value);
		}
	}

}
