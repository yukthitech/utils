package com.yukthitech.autox.test.assertion;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;

/**
 * Validation to Compare specified values for equality.
 * @author akiran
 */
@Executable(name = "assertEquals", group = Group.Common, message = "Compares specified values for euqality.")
public class AssertEqualsStep extends AbstractValidation
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

	/**
	 * Gets the type.
	 *
	 * @param val the val
	 * @return the type
	 */
	@SuppressWarnings("rawtypes")
	static String getType(Object val)
	{
		if(val == null)
		{
			return null;
		}
		
		String type = val.getClass().getName();
		
		if(val instanceof Collection)
		{
			type += " [Size: " + ((Collection) val).size() + "]";
		}
		else if(val instanceof Map)
		{
			type += " [Size: " + ((Map) val).size() + "]";
		}
		
		return type;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Comparing values for equlity. [Expected: {} [{}], Actual: {} [{}]]", 
				expected, getType(expected),  
				actual, getType(actual));

		boolean res = Objects.equals(expected, actual);
		exeLogger.debug("Result of comparision is: {}", res);

		return res;
	}
}
