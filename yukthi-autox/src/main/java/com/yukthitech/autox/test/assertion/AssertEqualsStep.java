package com.yukthitech.autox.test.assertion;

import java.util.Objects;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Validation to Compare specified values for equality.
 * @author akiran
 */
@Executable(name = "assertEquals", message = "Compares specified values for euqality.")
public class AssertEqualsStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value1 to compare.
	 */
	@Param(description = "Value1 to compare.", sourceType = SourceType.EXPRESSION)
	private Object value1;

	/**
	 * Value2 to compare.
	 */
	@Param(description = "Value2 to compare.", sourceType = SourceType.EXPRESSION)
	private Object value2;

	/**
	 * Sets the value1 to compare.
	 *
	 * @param value1 the new value1 to compare
	 */
	public void setValue1(Object value1)
	{
		this.value1 = value1;
	}

	/**
	 * Gets the value2 to compare.
	 *
	 * @return the value2 to compare
	 */
	public Object getValue2()
	{
		return value2;
	}

	/**
	 * Sets the value2 to compare.
	 *
	 * @param value2 the new value2 to compare
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
		exeLogger.debug(this, "Comparing values for equlity. [Value1: {}, Value2: {}]", value1, value2);

		boolean res = Objects.equals(value1, value2);
		exeLogger.debug(this, "Result of comparision is: {}", res);

		return res;

	}
}
