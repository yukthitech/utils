package com.yukthitech.autox.test.assertion;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Asserts specified values are not same reference.
 * @author akiran
 */
@Executable(name = "assertNotSame", message = "Asserts specified values are not same reference.")
public class AssertNotSameStep extends AbstractValidation
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Value1 to check.
	 */
	@Param(description = "Value1 to check.", sourceType = SourceType.EXPRESSION)
	private Object value1;

	/**
	 * Value2 to check.
	 */
	@Param(description = "Value2 to check.", sourceType = SourceType.EXPRESSION)
	private Object value2;

	/**
	 * Sets the value1 to check.
	 *
	 * @param value1 the new value1 to check
	 */
	public void setValue1(Object value1)
	{
		this.value1 = value1;
	}

	/**
	 * Gets the value2 to check.
	 *
	 * @return the value2 to check
	 */
	public Object getValue2()
	{
		return value2;
	}

	/**
	 * Sets the value2 to check.
	 *
	 * @param value2 the new value2 to check
	 */
	public void setValue2(Object value2)
	{
		this.value2 = value2;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(this, "Checkng the objects are NOT same [Value1: {}, Value2: {}]", value1, value2);
		boolean res = (value1 != value2);
		exeLogger.debug(this, "Result is {}", res);
		return res;
	}
}
