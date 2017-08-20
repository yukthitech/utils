package com.yukthitech.autox.test.common.steps;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.ConvertUtils;

/**
 * Sets the specified context attribute with specified value.
 * 
 * @author akiran
 */
@Executable(name = "setProperty", message = "Sets the specified context attribute with specified value using property.")
public class SetPropertyStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the attribute to set.")
	private String name;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Property value expression, which should be executed on source to get the value.")
	private String valueExpression;

	/** The source. */
	@Param(description = "Source on which property has to be evaluated.", sourceType = SourceType.OBJECT)
	private Object source;

	/**
	 * Type of the value to set. If specified, value will be converted to this
	 * type before setting on context.\nDefault: Sets as String.
	 */
	@Param(description = "Type of the value to set. If specified, value will be converted to this type before setting on context.\nDefault: Sets as String", required = false)
	private String type;

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param name the new name of attribute to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the value of the attribute to set.
	 *
	 * @param valueExpression the new value of the attribute to set
	 */
	public void setValueExpression(String valueExpression)
	{
		this.valueExpression = valueExpression;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the new source
	 */
	public void setSource(Object source)
	{
		this.source = source;
	}

	/**
	 * Sets the type of the value to set. If specified, value will be converted to this type before setting on context.\nDefault: Sets as String.
	 *
	 * @param type the new type of the value to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.IStep#execute(com.yukthitech.automation.AutomationContext, com.yukthitech.automation.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		Class<?> type = null;

		if(this.type != null)
		{
			try
			{
				type = Class.forName(this.type);
			} catch(Exception ex)
			{
				exeLogger.error("Invalid type specified for value conversion. Type - {}", this.type);
				throw new TestCaseFailedException("Invalid type specified for value conversion. Type - {}", this.type);
			}
		}
		
		Object sourceValue = AutomationUtils.parseObjectSource(context, exeLogger, source, null);
		
		Object value = null;
		
		try
		{
			exeLogger.debug("Fetching property '{}' from specified source", valueExpression);
			value = PropertyUtils.getProperty(sourceValue, valueExpression);
		}catch(Exception ex)
		{
			exeLogger.error(ex, "An error occurred while fetching property '{}' for object of type: {}", this.valueExpression, sourceValue.getClass().getName());
			throw new TestCaseFailedException("An error occurred while fetching property '{}' for object of type: {}", this.valueExpression, sourceValue.getClass().getName(), ex);
		}

		exeLogger.debug("Setting context attribute '{}' as value: {}", name, value);

		if(type != null)
		{
			exeLogger.debug("Converting value '{}' to type {} before setting on context", value, type.getName());
			value = ConvertUtils.convert(value, type);
		}

		context.setAttribute(name, value);
		return true;
	}
}
