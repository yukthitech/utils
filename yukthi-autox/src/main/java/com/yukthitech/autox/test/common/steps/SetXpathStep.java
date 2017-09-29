package com.yukthitech.autox.test.common.steps;

import org.apache.commons.jxpath.JXPathContext;

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
@Executable(name = "setXpath", message = "Sets the specified context attribute with specified value using jx path.")
public class SetXpathStep extends AbstractStep
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
	@Param(description = "Jx value expression, which should be executed on source to get the value.")
	private String valueExpression;

	/** The source. */
	@Param(description = "Source on which jx expression has to be evaluated.", sourceType = SourceType.OBJECT)
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
				exeLogger.error(this, "Invalid type specified for value conversion. Type - {}", this.type);
				throw new TestCaseFailedException("Invalid type specified for value conversion. Type - {}", this.type);
			}
		}
		
		Object sourceValue = AutomationUtils.parseObjectSource(context, exeLogger, source, null);
		
		exeLogger.debug(this, "Fetching xpath '{}' value from specified source", valueExpression);
		
		Object value = JXPathContext.newContext(sourceValue).getValue(valueExpression);

		exeLogger.debug(this, "Setting context attribute '{}' as value: {}", name, value);

		if(type != null)
		{
			exeLogger.debug(this, "Converting value '{}' to type {} before setting on context", value, type.getName());
			value = ConvertUtils.convert(value, type);
		}

		context.setAttribute(name, value);
		return true;
	}
}
