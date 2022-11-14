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
 * Asserts specified values are same reference.
 * @author akiran
 */
@Executable(name = "assertSame", group = Group.Common, message = "Asserts specified values are same reference.")
public class AssertSameStep extends AbstractValidation
{
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

	/**
	 * Gets the type.
	 *
	 * @param val the val
	 * @return the type
	 */
	private Class<?> getType(Object val)
	{
		if(val == null)
		{
			return null;
		}
		
		return val.getClass();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Comparing values to be same. [Expected: {} [{}], Actual: {} [{}]]", 
				expected, getType(expected),  
				actual, getType(actual));

		boolean isSame = (actual == expected);

		exeLogger.debug("Found values to be same: {}", isSame);


		if(!isSame)
		{
			AssertSameStep actualStep = (AssertSameStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found specified values to be different [Expected: {}, Actual: {}]", 
					actualStep.expected, actualStep.actual);
		}
	}
}
