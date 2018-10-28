package com.yukthitech.autox.common;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.JXPathContext;

import com.fasterxml.jackson.databind.JavaType;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

public enum ExpressionType
{
	PROP
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			try
			{
				return PropertyUtils.getProperty(context, value);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while evaluating bean-property '{}' on context", value, ex);
			}
		}
	},
	
	XPATH
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			try
			{
				return JXPathContext.newContext(context).getValue(value);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while evaluating bean-property '{}' on context", value, ex);
			}
		}
	},
	
	REF
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			try
			{
				return PropertyUtils.getProperty(context, value);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while evaluating bean-property '{}' on context", value, ex);
			}
		}
	},

	STRING
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			return value.trim();
		}
	},
	
	INT
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			value = value.trim();
			
			try
			{
				return Integer.parseInt(value);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while converting specified value into int. Value: " + value, ex);
			}
		}
	},
	
	LONG
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			value = value.trim();
			
			try
			{
				return Long.parseLong(value);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while converting specified value into long. Value: " + value, ex);
			}
		}
	},

	FLOAT
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			value = value.trim();
			
			try
			{
				return Float.parseFloat(value);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while converting specified value into float. Value: " + value, ex);
			}
		}
	},

	DOUBLE
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			value = value.trim();
			
			try
			{
				return Double.parseDouble(value);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while converting specified value into double. Value: " + value, ex);
			}
		}
	},

	BOOLEAN
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			value = value.trim();
			return "true".equalsIgnoreCase(value);
		}
	},

	CONDITION
	{
		@Override
		public Object parse(AutomationContext context, String value, JavaType resultType)
		{
			value = value.trim();
			return AutomationUtils.evaluateCondition(context, value);
		}
	},

	;
	
	public abstract Object parse(AutomationContext context, String value, JavaType resultType);
}
