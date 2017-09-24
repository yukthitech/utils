package com.yukthitech.autox.test.storage.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.AutomationUtils;

/**
 * Stores specified key value into persistence storage. Which can be used across the executions.
 * @author akiran
 */
@Executable(name = "store", message = "Stores specified key value into persistence storage. Which can be used across the executions.")
public class StoreStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Key to be stored.
	 */
	@Param(description = "Key to be stored.", required = true)
	private String key;
	
	/**
	 * Value to be stored.
	 */
	@Param(description = "Value to be strored.", required = true, sourceType = SourceType.OBJECT)
	private String value;
	
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
	 * Sets the value to be stored.
	 *
	 * @param value the new value to be stored
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) 
	{
		exeLogger.debug("Storing key '{}' with value - {}", key, value);
		
		Object sourceValue = context;
		
		if(value != null)
		{
			sourceValue = AutomationUtils.parseObjectSource(context, exeLogger, value, null);
		}
		
		context.getPersistenceStorage().set(key, sourceValue);
		return true;
	}
}
