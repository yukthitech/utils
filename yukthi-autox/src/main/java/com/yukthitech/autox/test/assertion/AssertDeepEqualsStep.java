package com.yukthitech.autox.test.assertion;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.DeepEqualsUtil;

/**
 * Validation to Compare specified values for deep equality.
 * @author akiran
 */
@Executable(name = "assertDeepEquals", group = Group.Common, message = "Compares specified values for deep equality. This will not compare the java types, but compares only the structure.")
public class AssertDeepEqualsStep extends AbstractValidation
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
	 * If true, extra properties in actual will be ignored and will only ensure expected
	 * structure is found in actual object.
	 */
	@Param(description = "If true, extra properties in actual will be ignored and will only ensure expected structure is found in actual object. Default: false")
	private boolean ignoreExtraProperties = false;

	@Param(description = "If false, instead of checking for equlity, check will be done for non equality. Default: true")
	private boolean checkEquality = true;
	
	@Param(description = "Failed path, if any, will be set on context with this attribute. Default: failedPath", attrName = true, defaultValue = "failedPath")
	private String failedPathAttr = "failedPath";

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

	public void setIgnoreExtraProperties(boolean ignoreExtraProperties)
	{
		this.ignoreExtraProperties = ignoreExtraProperties;
	}
	
	public void setCheckEquality(boolean checkEquality)
	{
		this.checkEquality = checkEquality;
	}

	public void setFailedPathAttr(String failedPathAttr)
	{
		this.failedPathAttr = failedPathAttr;
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
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Comparing values for deep-equlity. [\nExpected: {} [{}], \nActual: {} [{}], \nIgnore exta Properties: {}]", 
				expected, getType(expected),  
				actual, getType(actual),
				ignoreExtraProperties);
		
		String diffPath = DeepEqualsUtil.deepCompare(this.actual, this.expected, ignoreExtraProperties, context, exeLogger);
		boolean res = (diffPath == null);
		
		context.setAttribute(failedPathAttr, diffPath);
		
		exeLogger.debug("Result of comparision is: {}", res);
		
		if(!checkEquality)
		{
			res = !res;
			exeLogger.debug("As non-equality has to be checked, setting final result as: {}", res);
		}

		return res;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true, (Class) this.getClass());
	}
}
