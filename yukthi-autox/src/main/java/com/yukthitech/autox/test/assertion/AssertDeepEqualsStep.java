package com.yukthitech.autox.test.assertion;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AbstractValidation;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Validation to Compare specified values for deep equality.
 * @author akiran
 */
@Executable(name = "assertDeepEquals", message = "Compares specified values for deep equality. This will not compare the java types, but compares only the structure.")
public class AssertDeepEqualsStep extends AbstractValidation
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	private static ObjectMapper objectMapper = new ObjectMapper();

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
	
	@Param(description = "Failed path, if any, will be set on context with this attribute. Default: failedPath")
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
	 * Converts input object into map of maps using json.
	 * @param obj
	 * @return
	 */
	private Object toJsonObject(Object obj)
	{
		try
		{
			String json = objectMapper.writeValueAsString(obj);
			return objectMapper.readValue(json, Object.class);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting object into json object: {}", obj);
		}
	}
	
	private boolean deepCompare(Map<String, Object> actual, Map<String, Object> expected, String propPath, AutomationContext context, ExecutionLogger logger)
	{
		if(!ignoreExtraProperties)
		{
			if(actual.size() != expected.size())
			{
				context.setAttribute(failedPathAttr, propPath);
				
				logger.debug("Comparision failed because of non-matching map-size at path: {} [Actual's size: {}, Expected's size: {}]", 
						propPath, actual.size(), expected.size());
				return false;
			}
		}
		
		Object expectedVal = null, actualVal = null;
		
		for(String key : expected.keySet())
		{
			expectedVal = expected.get(key);
			actualVal = actual.get(key);
			
			if(!deepCompare(actualVal, expectedVal, propPath + "." + key, context, logger))
			{
				return false;
			}
		}
		
		if(ignoreExtraProperties)
		{
			return true;
		}
		
		for(String key : actual.keySet())
		{
			//if a key present in actual is not present in expected
			// Note: common properties are already verified.
			if(!expected.containsKey(key))
			{
				return false;
			}
		}

		return true;
	}

	private boolean deepCompare(List<Object> actual, List<Object> expected, String propPath, AutomationContext context, ExecutionLogger logger)
	{
		if(!ignoreExtraProperties)
		{
			if(actual.size() != expected.size())
			{
				context.setAttribute(failedPathAttr, propPath);
				
				logger.debug("Comparision failed because of non-matching list-size at path: {} [Actual's size: {}, Expected's size: {}]", 
						propPath, actual.size(), expected.size());
				return false;
			}
		}
		
		Object expectedVal = null, actualVal = null;
		int size = expected.size();
		
		//if the actual size is less than actual size, return false
		if(actual.size() < size)
		{
			context.setAttribute(failedPathAttr, propPath);
			
			logger.debug("Comparision failed because of non-matching list-size at path: {} [Actual's size: {}, Expected's size: {}]", 
					propPath, actual.size(), expected.size());
			return false;
		}

		for(int i = 0; i < size; i++)
		{
			expectedVal = expected.get(i);
			actualVal = actual.get(i);
			
			if(!deepCompare(actualVal, expectedVal, propPath + "[" + i + "]", context, logger))
			{
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean deepCompare(Object actual, Object expected, String propPath, AutomationContext context, ExecutionLogger logger)
	{
		//if both are null
		if(actual == null && expected == null)
		{
			return true;
		}
		
		//if both are not null but one of object is null
		if(actual == null || expected == null)
		{
			context.setAttribute(failedPathAttr, propPath);
			
			logger.debug("Comparision failed because of null value at path: {} [Actual is Null: {}, Expected is Null: {}]", 
					propPath, (actual == null), (expected == null));
			return false;
		}
		
		if(!actual.getClass().equals(expected.getClass()))
		{
			context.setAttribute(failedPathAttr, propPath);
			
			logger.debug("Comparision failed because of incompatible types at path: {} [Actual's type: {}, Expected's type: {}]", 
					propPath, actual.getClass().getName(), expected.getClass().getName());
			return false;
		}
		
		if(actual instanceof Map)
		{
			return deepCompare((Map<String, Object>) actual, (Map<String, Object>) expected, propPath, context, logger);
		}
		
		if(actual instanceof List)
		{
			return deepCompare((List<Object>) actual, (List<Object>) expected, propPath, context, logger);
		}

		boolean res = Objects.equals(actual, expected);
		
		if(!res)
		{
			logger.debug("Comparision failed because of non-equal values at path: {} [Actual Val: {}, Expected Val: {}]", 
					propPath, actual, expected);
		}
		
		return res;
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
		
		//if both are null
		if(this.actual == null && this.expected == null)
		{
			return true;
		}
		
		//if both are not null but one of object is null
		if(this.actual == null || this.expected == null)
		{
			return false;
		}
		
		Object actual = toJsonObject(this.actual);
		Object expected = toJsonObject(this.expected);

		boolean res = deepCompare(actual, expected, "$", context, exeLogger);
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
