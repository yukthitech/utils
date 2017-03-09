package com.yukthitech.automation.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.automation.Param;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Used to capture the expected exception details for a test case.
 * @author akiran
 */
public class ExceptionExpected
{
	/**
	 * Expected property of the test case.
	 * @author akiran
	 */
	public static class Property
	{
		/**
		 * Name of the property.
		 */
		private String name;
		
		/**
		 * Value of the property.
		 */
		private String value;

		/**
		 * Sets the name of the property.
		 *
		 * @param name the new name of the property
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Sets the value of the property.
		 *
		 * @param value the new value of the property
		 */
		public void setValue(String value)
		{
			this.value = value;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(name);
			builder.append(" = ").append(value);
			
			return builder.toString();
		}
	}
	
	/**
	 * Type of exception expected.
	 */
	@Param(description = "Type of exception expected.")
	private Class<?> type;
	
	/**
	 * Expected properties (key-value) pairs of the exception.
	 */
	@Param(description = "Expected properties (key-value) pairs of the exception.", required = false)
	private List<Property> properties = new ArrayList<>();
	
	/**
	 * Sets the type of exception expected.
	 *
	 * @param type the new type of exception expected
	 */
	public void setType(Class<?> type)
	{
		if(!Exception.class.isAssignableFrom(type))
		{
			throw new InvalidArgumentException("Invalid exception type specified: {}", type.getName());
		}
		
		this.type = type;
	}
	
	/**
	 * Gets the type of exception expected.
	 *
	 * @return the type of exception expected
	 */
	public Class<?> getType()
	{
		return type;
	}
	
	/**
	 * Adds expected property to this exception details.
	 * @param property property to add
	 */
	public void addProperty(Property property)
	{
		this.properties.add(property);
	}
	
	/**
	 * Validates the provided exception is matching with current expected exception.
	 * @param ex Actual exception object.
	 */
	public void validateMatch(Exception ex)
	{
		if(!type.equals(ex.getClass()))
		{
			throw new InvalidArgumentException(ex, "Expected exception type {} and actual exception type are not matching", type.getName(), ex.getClass().getName());
		}
		
		String actualValue = null;
		
		for(Property prop : properties)
		{
			try
			{
				actualValue = BeanUtils.getProperty(ex, prop.name);
			}catch(Exception bex)
			{
				throw new InvalidArgumentException(bex, "An error occurred while fetching property '{}' of exception: {}", prop.name, ex.getClass().getName());
			}
			
			if(prop.value == null)
			{
				if(actualValue != null)
				{
					throw new InvalidArgumentException(ex, "Property value of exception is not matching expected value [Exception: {}, Property: {}, Actual Value: {}, Expected Value: {}]", 
							type.getName(), prop.name, actualValue, prop.value);
				}
			}
			else if(!prop.value.equals(actualValue))
			{
				throw new InvalidArgumentException(ex, "Property value of exception is not matching expected value [Exception: {}, Property: {}, Actual Value: {}, Expected Value: {}]", 
						type.getName(), prop.name, actualValue, prop.value);
			}
		}
	}
}
