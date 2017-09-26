package com.yukthitech.autox.test.common.steps;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;

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

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		Object bean = context.getAttribute(this.beanAttr);
		
		if(bean == null)
		{
			exeLogger.debug("No value found on context with name '{}'. Hence no action would be taken.", this.beanAttr);
			return true;
		}

		exeLogger.debug("Setting property '{}' of bean '{}' as value: {}", property, this.beanAttr, value);
		
		Object value = AutomationUtils.parseObjectSource(context, exeLogger, this.value, null);
		PropertyUtils.setProperty(bean, property, value);
		
		return true;
	}
}
