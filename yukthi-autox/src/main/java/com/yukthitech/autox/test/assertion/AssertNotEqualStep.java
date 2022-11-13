package com.yukthitech.autox.test.assertion;

import com.google.common.base.Objects;
import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Non validation step, to compares specified values for non equality.
 * @author akiran
 */
@Executable(name = "assertNotEquals", group = Group.Common, message = "Compares specified values for non euqality.")
public class AssertNotEqualStep extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Value1 for comparison.
	 */
	@Param(description = "Expected value for comparison.", sourceType = SourceType.EXPRESSION)
	private Object expected;

	/**
	 * Value2 for comparison
	 */
	@Param(description = "Actual value for comparison", sourceType = SourceType.EXPRESSION)
	private Object actual;

	/**
	 * Sets the value1 for comparison.
	 *
	 * @param expected the new value1 for comparison
	 */
	public void setExpected(Object expected)
	{
		this.expected = expected;
	}
	
	/**
	 * Sets the value2 for comparison.
	 *
	 * @param actual the new value2 for comparison
	 */
	public void setActual(Object actual)
	{
		this.actual = actual;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug(false, "Comparing values for NON equality. <span style=\"white-space: pre-wrap\">[Expected: {} [{}], Actual: {} [{}]]</span>", 
				expected, AssertEqualsStep.getType(expected),  
				actual, AssertEqualsStep.getType(actual));

		boolean isEqual = Objects.equal(expected, actual);
		
		if(isEqual)
		{
			exeLogger.debug("Found specified objects to be EQUAL");
			
			AssertNotEqualStep actualStep = (AssertNotEqualStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found specified values are same. [Expected: {}, Actual: {}]", 
					actualStep.expected, actualStep.expected);
		}
		
		exeLogger.debug("Found specified objects to be UNEQUAL");
	}
}
