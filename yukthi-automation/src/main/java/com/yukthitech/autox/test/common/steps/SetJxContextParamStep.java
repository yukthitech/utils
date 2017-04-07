package com.yukthitech.autox.test.common.steps;

import org.apache.commons.jxpath.JXPathContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.utils.ConvertUtils;

/**
 * Sets the specified context attribute with specified value.
 * 
 * @author akiran
 */
@Executable(name = "setJxContextParam", message = "Sets the specified context attribute with specified value using jx path.")
public class SetJxContextParamStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
	@Param(description = "Source on which jx expression has to be evaluated.")
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
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		Class<?> type = null;

		if(this.type != null)
		{
			try
			{
				type = Class.forName(this.type);
			} catch(Exception ex)
			{
				throw new TestCaseFailedException("Invalid type specified for value conversion. Type - {}", this.type);
			}
		}
		
		Object sourceValue = null;
		
		if(source instanceof String)
		{
			try
			{
				sourceValue = OBJECT_MAPPER.readValue( (String) source, Object.class );
			}catch(Exception ex)
			{
				throw new IllegalStateException("An exception occurred while parsing source: \n" + source, ex);
			}
		}
		else
		{
			sourceValue = source;
		}
		
		Object value = JXPathContext.newContext(sourceValue).getValue(valueExpression);

		exeLogger.debug("Setting context attribute '{}' as value: {}", name, value);

		if(type != null)
		{
			exeLogger.debug("Converting value '{}' to type {} before setting on context", value, type.getName());
			value = ConvertUtils.convert(value, type);
		}

		context.setAttribute(name, value);
	}
}
