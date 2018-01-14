package com.yukthitech.autox.test.common.steps;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.utils.ConvertUtils;

/**
 * Changes the property value of specified context bean with specified value.
 * 
 * @author akiran
 */
@Executable(name = "setBeanProperty", message = "Changes the property value of specified context bean with specified value.")
public class SetBeanProperty extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of attribute to set.
	 */
	@Param(description = "Name of the bean attribute whose property needs to be modified.")
	private String beanAttr;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Property of the bean to be set")
	private String property;

	/**
	 * Value of the property to set.
	 */
	@Param(description = "Value of the property to set. Default: null", required = false, sourceType = SourceType.OBJECT)
	private Object value;
	
	/**
	 * String value of the property to set. Default: null.
	 */
	@Param(description = "String value of the property to set. Default: null", required = false)
	private String valueStr;

	/**
	 * Sets the name of attribute to set.
	 *
	 * @param bean the new name of attribute to set
	 */
	public void setBeanAttr(String bean)
	{
		this.beanAttr = bean;
	}

	/**
	 * Sets the value of the attribute to set.
	 *
	 * @param property the new value of the attribute to set
	 */
	public void setProperty(String property)
	{
		this.property = property;
	}

	/**
	 * Sets the value of the property to set.
	 *
	 * @param valueObject the new value of the property to set
	 */
	public void setValue(Object valueObject)
	{
		this.value = valueObject;
	}
	
	/**
	 * Sets the string value of the property to set. Default: null.
	 *
	 * @param valueStr the new string value of the property to set
	 */
	public void setValueStr(String valueStr)
	{
		this.valueStr = valueStr;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		Object bean = context.getAttribute(this.beanAttr);
		
		if(bean == null)
		{
			exeLogger.debug(this, "No value found on context with name '{}'. Hence no action would be taken.", this.beanAttr);
			return true;
		}

		if(this.value != null)
		{
			exeLogger.debug(this, "Setting property '{}' of bean '{}' as value: {}", property, this.beanAttr, value);
			Object value = AutomationUtils.parseObjectSource(context, exeLogger, this.value, null);
			PropertyUtils.setProperty(bean, property, value);
			return true;
		}
		
		if(this.valueStr != null)
		{
			exeLogger.debug(this, "Setting property '{}' of bean '{}' as value: {}", property, this.beanAttr, valueStr);

			Class<?> valueType = PropertyUtils.getPropertyType(bean, property);
			Object value = ConvertUtils.convert(valueStr, valueType);
			
			PropertyUtils.setProperty(bean, property, value);
		}
		
		return true;
	}
}
