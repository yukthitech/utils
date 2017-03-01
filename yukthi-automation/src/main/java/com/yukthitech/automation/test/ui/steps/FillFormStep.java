package com.yukthitech.automation.test.ui.steps;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.SeleniumConfiguration;
import com.yukthitech.automation.test.ui.common.UiAutomationUtils;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to fill the target form with specified data.
 * 
 * @author akiran
 */
@Executable(value = "fillForm", requiredConfigurationTypes = SeleniumConfiguration.class, message = "Fills the form with specified data")
public class FillFormStep implements IStep
{
	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(FillFormStep.class);

	/** 
	 * The error message. 
	 **/
	private static String DEBUG_MESSAGE = "Populating field {} with value - {}";
	
	/** 
	 * The error message. 
	 **/
	private static String ERROR_MESSAGE = "Failed to fill element '{}' under parent '{}' with value - {}";
	
	/**
	 * Html locator of the form or container (like DIV) enclosing the input
	 * elements.
	 */
	private String locator;

	/**
	 * Data to be filled. All the fields matching with the property names of
	 * specified bean will be searched and populated with corresponding data.
	 */
	private Object data;

	/**
	 * Fills the form using standard bean properties.
	 * 
	 * @param context
	 *            Automation context.
	 * @param exeLogger
	 *            Logger to be used.
	 */
	private void fillWithStandardBean(AutomationContext context, IExecutionLogger exeLogger)
	{
		WebElement parentElement = UiAutomationUtils.findElement(context, null, locator);

		PropertyDescriptor propDescLst[] = PropertyUtils.getPropertyDescriptors(data.getClass());
		Object value = null;

		for(PropertyDescriptor desc : propDescLst)
		{
			// ignore java core properties like - getClass()
			if(desc.getReadMethod() == null || desc.getReadMethod().getDeclaringClass().getName().startsWith("java."))
			{
				continue;
			}

			try
			{
				value = PropertyUtils.getProperty(data, desc.getName());

				if(value == null)
				{
					continue;
				}
			} catch(Exception ex)
			{
				logger.error("An error occurred while setting property - " + desc.getName(), ex);
				continue;
			}

			exeLogger.debug(DEBUG_MESSAGE, desc.getName(), value);

			if(!UiAutomationUtils.populateField(context, parentElement, desc.getName(), "" + value))
			{
				exeLogger.error(ERROR_MESSAGE, desc.getName(), value);
				throw new InvalidStateException(ERROR_MESSAGE, desc.getName(), value);
			}
		}
	}

	/**
	 * Fills the form using dynamic bean.
	 * 
	 * @param context
	 *            Automation context
	 * @param exeLogger
	 *            logger
	 */
	private void fillWithDynamicBean(AutomationContext context, IExecutionLogger exeLogger)
	{
		WebElement parentElement = UiAutomationUtils.findElement(context, null, locator);

		DynamicBean dynamicBean = (DynamicBean) data;
		Map<String, Object> properties = dynamicBean.getProperties();
		Object value = null;

		for(String name : properties.keySet())
		{
			value = properties.get(name);

			// ignore java core properties like - getClass()
			if(properties.get(name) == null)
			{
				continue;
			}

			exeLogger.debug(DEBUG_MESSAGE, name, value);

			if(!UiAutomationUtils.populateField(context, parentElement, name, "" + value))
			{
				exeLogger.error(ERROR_MESSAGE, name, value);
				throw new InvalidStateException(ERROR_MESSAGE, name, locator, value);
			}
		}
	}

	/**
	 * Loops throw the properties specified data bean and populates the fields
	 * with matching names.
	 * 
	 * @param context
	 *            Current automation context
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		if(data instanceof DynamicBean)
		{
			fillWithDynamicBean(context, exeLogger);
		}
		else
		{
			fillWithStandardBean(context, exeLogger);
		}
	}

	/**
	 * Gets the html locator of the form or container (like DIV) enclosing the
	 * input elements.
	 *
	 * @return the html locator of the form or container (like DIV) enclosing
	 *         the input elements
	 */
	public String getLocator()
	{
		return locator;
	}

	/**
	 * Sets the html locator of the form or container (like DIV) enclosing the
	 * input elements.
	 *
	 * @param locator
	 *            the new html locator of the form or container (like DIV)
	 *            enclosing the input elements
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}

	/**
	 * Gets the data to be filled. All the fields matching with the property
	 * names of specified bean will be searched and populated with corresponding
	 * data.
	 *
	 * @return the data to be filled
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * Sets the data to be filled. All the fields matching with the property
	 * names of specified bean will be searched and populated with corresponding
	 * data.
	 *
	 * @param data
	 *            the new data to be filled
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Locator: ").append(locator);
		builder.append(",").append("Data: ").append(data);

		builder.append("]");
		return builder.toString();
	}
}
