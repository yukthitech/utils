package com.yukthitech.autox.test.assertion;

import java.util.Objects;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Asserts the value for not null.
 * @author akiran
 */
@Executable(name = "assertNotNull", message = "Asserts the specified value is not null.")
public class AssertNotNullStep extends AbstractValidation
{
	
	/**
	 * The Constant serialVersionUID.
	 */
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
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(this, "Checking the value is not null [value : {}]", value);
		boolean res = Objects.nonNull(value);
		exeLogger.debug(this, "Result is {}", res);
		return res;
	}
}
