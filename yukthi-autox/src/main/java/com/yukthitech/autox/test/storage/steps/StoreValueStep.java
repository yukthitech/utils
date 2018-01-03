package com.yukthitech.autox.test.storage.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;

/**
 * Fetches value from store for specified key.
 * 
 * @author akiran
 */
@Executable(name = {"storeGet", "storeValue"}, message = "Fetches value from store for specified key.")
public class StoreValueStep extends AbstractStep
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Key to be stored.
	 */
	@Param(description = "Key to be stored.", required = true)
	private String key;

	/**
	 * Name of the attribute to be used to set value on context
	 */
	@Param(description = "Name of the attribute to be used to set value on context", required = true)
	private String attribute;

	/**
	 * Sets the key to be stored.
	 *
	 * @param key the new key to be stored
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * Sets the name of the attribute to be used to set value on context.
	 *
	 * @param attribute the new name of the attribute to be used to set value on context
	 */
	public void setAttribute(String attribute)
	{
		this.attribute = attribute;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		Object value = context.getPersistenceStorage().get(key);

		if(value == null)
		{
			return true;
		}

		context.setAttribute(attribute, value);
		return true;
	}
}
