package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Sets the specified context attribute with specified value.
 * 
 * @author akiran
 */
@Executable(name = "set", message = "Sets the specified context attribute with specified value")
public class SetStep extends AbstractStep
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
	@Param(description = "Value of the attribute to set. Default: empty string", required = false)
	private String value;
	
	/**
	 * Value of the attribute to set in the form of Object.
	 */
	@Param(description = "Value of the attribute to set. Default: empty string", required = false, sourceType = SourceType.OBJECT)
	private Object valueObject;

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
	 * Sets the value of the attribute to set in the form of Object.
	 *
	 * @param valueObject the new value of the attribute to set in the form of Object
	 */
	public void setValueObject(Object valueObject)
	{
		this.valueObject = valueObject;
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
	
	/**
	 * Gets the final resultant value among 'value' and 'valueObject'.
	 * @param context
	 * @param exeLogger
	 * @return
	 */
	private Object getValue(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(value != null)
		{
			return value;
		}
		
		if(valueObject != null)
		{
			exeLogger.debug(this, "Parsing value object: {}", this.valueObject);
			return AutomationUtils.parseObjectSource(context, exeLogger, this.valueObject, null);
		}
		
		return "";
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		Object value = this.getValue(context, exeLogger);
		
		if(this.type != null)
		{
			try
			{
				exeLogger.debug(this, "Converting value '{}' to type {} before setting on context", value, type);
				value = AutomationUtils.convert(value, type);
			}catch(Exception ex)
			{
				exeLogger.error(this, "Invalid type specified for value conversion. Type - {}", this.type);
				throw new TestCaseFailedException(this, "Invalid type specified for value conversion. Type - {}", this.type);
			}
		}
		
		exeLogger.debug(this, "Setting context attribute '{}' as value: {}", name, value);
		
		context.setAttribute(name, value);
		return true;
	}
}
