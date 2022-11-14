package com.yukthitech.autox.test.assertion;

import java.util.Objects;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Asserts the value for not null.
 * @author akiran
 */
@Executable(name = "assertNotNull", group = Group.Common, message = "Asserts the specified value is not null.")
public class AssertNotNullStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value to check.
	 */
	@Param(description = "Value to check.", sourceType = SourceType.EXPRESSION)
	private Object value;

	/**
	 * Sets the value to check.
	 *
	 * @param value the new value to check
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
		exeLogger.debug("Checking the value is not null [value : {}]", value);
		
		boolean nonNull = Objects.nonNull(value);
		exeLogger.debug("Found value to be non-null: {}", nonNull);
		
		if(!nonNull)
		{
			AssertNotNullStep actualStep = (AssertNotNullStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found specified value to be null: {}", 
					actualStep.value);
		}
	}
}
