package com.yukthitech.automation.test.common.steps;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.test.TestCaseFailedException;
import com.yukthitech.utils.ConvertUtils;

/**
 * Sets the specified context attribute with specified value.
 * 
 * @author akiran
 */
@Executable(name = "setContextParam", message = "Sets the specified context attribute with specified value")
public class SetContextParamStep extends AbstractStep
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
	@Param(description = "Value of the attribute to set.")
	private String value;
	
	/**
	 * Type of the value to set. If specified, value will be converted to this type before setting on context.\nDefault: Sets as String.
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
	 * @param value the new value of the attribute to set
	 */
	public void setValue(String value)
	{
		this.value = value;
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

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		Class<?> type = null;
		
		if(this.type != null)
		{
			try
			{
				type = Class.forName(this.type);
			}catch(Exception ex)
			{
				throw new TestCaseFailedException("Invalid type specified for value conversion. Type - {}", this.type);
			}
		}
		
		Object value = this.value;
		
		exeLogger.debug("Setting context attribute '{}' as value: {}", name, value);
		
		if(type != null)
		{
			exeLogger.debug("Converting value '{}' to type {} before setting on context", value, type.getName());
			value = ConvertUtils.convert(value, type);
		}
		
		context.setAttribute(name, value);
	}
}
