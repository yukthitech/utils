package com.yukthitech.autox.test;

import java.util.concurrent.CountDownLatch;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Custom ui locator used to customized way of handling ui 
 * elements in an app.
 * @author akiran
 */
public class CustomUiLocator extends AbstractLocationBased
{
	/**
	 * Name of this locator. The same has to be used as prefix in locators.
	 */
	private String name;
	
	/**
	 * Description about this locator.
	 */
	private String description;
	
	/**
	 * Function to get value out of this type of locator.
	 */
	private Function getter;
	
	/**
	 * Function to set value into this type of locator.
	 */
	private Function setter;
	
	/**
	 * Gets the name of this locator. The same has to be used as prefix in locators.
	 *
	 * @return the name of this locator
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this locator. The same has to be used as prefix in locators.
	 *
	 * @param name the new name of this locator
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description about this locator.
	 *
	 * @return the description about this locator
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about this locator.
	 *
	 * @param description the new description about this locator
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the function to get value out of this type of locator.
	 *
	 * @return the function to get value out of this type of locator
	 */
	public Function getGetter()
	{
		return getter;
	}

	/**
	 * Sets the function to get value out of this type of locator.
	 *
	 * @param getter the new function to get value out of this type of locator
	 */
	public void setGetter(Function getter)
	{
		this.getter = getter;
	}

	/**
	 * Gets the function to set value into this type of locator.
	 *
	 * @return the function to set value into this type of locator
	 */
	public Function getSetter()
	{
		return setter;
	}

	/**
	 * Sets the function to set value into this type of locator.
	 *
	 * @param setter the new function to set value into this type of locator
	 */
	public void setSetter(Function setter)
	{
		this.setter = setter;
	}
	
	public boolean setValue(String query, Object value)
	{
		AutomationContext context = AutomationContext.getInstance();
		CountDownLatch latch = new CountDownLatch(1);
		ObjectWrapper<Boolean> resWrapper = new ObjectWrapper<Boolean>(false);
		
		setter.execute(context, context.getExecutionLogger(), resObj -> 
		{
			boolean res = true;

			if(resObj != null && "false".equalsIgnoreCase(resObj.toString()))
			{
				res = false;
			}
			
			resWrapper.setValue(res);
			latch.countDown();
		});
		
		try
		{
			latch.await();
		} catch(InterruptedException ex)
		{
			throw new InvalidStateException("Thread is interrupted", ex);
		}
		
		return resWrapper.getValue();
	}
	
	public String getValue(String query)
	{
		AutomationContext context = AutomationContext.getInstance();
		CountDownLatch latch = new CountDownLatch(1);
		ObjectWrapper<String> resWrapper = new ObjectWrapper<String>();
		
		setter.execute(context, context.getExecutionLogger(), resObj -> 
		{
			String res = null;

			if(resObj != null)
			{
				res = resObj.toString();
			}
			
			resWrapper.setValue(res);
		});
		
		
		try
		{
			latch.await();
		} catch(InterruptedException ex)
		{
			throw new InvalidStateException("Thread is interrupted", ex);
		}
		
		return resWrapper.getValue();
	}
}
