package com.yukthitech.automation;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Base class for validations.
 */
public abstract class AbstractValidation implements IValidation
{
	/**
	 * Pattern used to replace expressions in step properties.
	 */
	private static Pattern CONTEXT_EXPR_PATTERN = Pattern.compile("\\{\\{(.+)\\}\\}");
	
	/**
	 * Failure message for the validation.
	 */
	private String failureMessage;

	/**
	 * Sets the failure message for the validation.
	 *
	 * @param failureMessage the new failure message for the validation
	 */
	public void setFailureMessage(String failureMessage)
	{
		this.failureMessage = failureMessage;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.IValidation#getFailureMessage()
	 */
	@Override
	public String getFailureMessage()
	{
		return failureMessage;
	}
	
	/**
	 * Replaces expressions in specified step properties.
	 * @param context Context to fetch values for expressions.
	 * @param validation Validation in which expression has to be replaced
	 */
	public void replaceExpressions(AutomationContext context, AbstractValidation validation)
	{
		Field fields[] = validation.getClass().getDeclaredFields();
		String value = null;
		String propertyExpr = null;
		
		Matcher matcher = null;
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Object> contextAttr = context.getAttributeMap();
		
		for(Field field : fields)
		{
			//ignore non string fields
			if(!String.class.equals(field.getType()))
			{
				continue;
			}

			try
			{
				field.setAccessible(true);
				
				value = (String) field.get(validation);
				
				//ignore null field values
				if(value == null)
				{
					continue;
				}
				
				matcher = CONTEXT_EXPR_PATTERN.matcher(value);
				buffer.setLength(0);
	
				//replace the expressions in the field value
				while(matcher.find())
				{
					propertyExpr = matcher.group(1);
					
					matcher.appendReplacement(buffer, BeanUtils.getProperty(contextAttr, propertyExpr));
				}
				
				matcher.appendTail(buffer);
				
				//set the result string back to field
				field.set(validation, buffer.toString());
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while parsing expressions in field '{}' in class - {}", 
					field.getName(), validation.getClass().getName());
			}
		}
	}
}
