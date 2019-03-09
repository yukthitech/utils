package com.yukthitech.autox.test.assertion;

import java.util.Objects;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Asserts the value to be null.
 * @author akiran
 */
@Executable(name = "assertNull", message = "Asserts the value to be null.")
public class AssertNullStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value to check.
	 */
	@Param(description = "Value to check.", sourceType = SourceType.EXPRESSION)
	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Checking the value is null [value : {}]", value);
		boolean res = Objects.isNull(value);
		exeLogger.debug("Result is {}", res);
		return res;
	}
}
