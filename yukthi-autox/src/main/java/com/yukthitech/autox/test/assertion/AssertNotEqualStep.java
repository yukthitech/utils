package com.yukthitech.autox.test.assertion;

import com.google.common.base.Objects;
import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
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
	private static final long serialVersionUID = 1L;

	/**
	 * Value1 for comparison.
	 */
	@Param(description = "Value1 for comparison.", sourceType = SourceType.EXPRESSION)
	private Object value1;

	/**
	 * Value2 for comparison
	 */
	@Param(description = "Value2 for comparison", sourceType = SourceType.EXPRESSION)
	private Object value2;

	public void setValue1(Object value1)
	{
		this.value1 = value1;
	}
	
	public void setValue2(Object value2)
	{
		this.value2 = value2;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug(false, "Comparing values for NON equlity. <span style=\"white-space: pre-wrap\">[Value1: {} [{}], Value2: {} [{}]]</span>", 
				value1, AssertEqualsStep.getType(value1),  
				value2, AssertEqualsStep.getType(value2));

		boolean isEqual = Objects.equal(value1, value2);
		
		if(isEqual)
		{
			exeLogger.debug("Found specified objects to be EQUAL");
			
			AssertNotEqualStep actualStep = (AssertNotEqualStep) super.sourceStep;
			throw new AutoxValidationException(this, "Found specified values are same. [Value1: {}, Value2: {}]", 
					actualStep.value1, actualStep.value2);
		}
		
		exeLogger.debug("Found specified objects to be UNEQUAL");
	}
}
