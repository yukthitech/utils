package com.yukthitech.autox.test.assertion;

import com.google.common.base.Objects;
import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Non validation step, to compares specified values for non equality.
 * @author akiran
 */
@Executable(name = "assertNotEquals", group = Group.Common, message = "Compares specified values for non euqality.")
public class AssertNotEqualStep extends AbstractValidation
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Expected value in comparison..
	 */
	@Param(description = "Expected value in comparison.", sourceType = SourceType.EXPRESSION)
	private Object expected;

	/**
	 * Actual value in comparison.
	 */
	@Param(description = "Actual value in comparison", sourceType = SourceType.EXPRESSION)
	private Object actual;

	/**
	 * Sets the expected value in comparison..
	 *
	 * @param expected the new expected value in comparison
	 */
	public void setExpected(Object expected)
	{
		this.expected = expected;
	}

	/**
	 * Sets the actual value in comparison.
	 *
	 * @param actual the new actual value in comparison
	 */
	public void setActual(Object actual)
	{
		this.actual = actual;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(false, "Comparing values for NON equlity. <span style=\"white-space: pre-wrap\">[Expected: {} [{}], Actual: {} [{}]]</span>", 
				expected, AssertEqualsStep.getType(expected),  
				actual, AssertEqualsStep.getType(actual));

		boolean res = !Objects.equal(actual, expected);
		exeLogger.debug("Result is {}", res);
		return res;
	}
}
