package com.yukthitech.autox.test.storage.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;

/**
 * Deletes value from store for specified key.
 * 
 * @author akiran
 */
@Executable(name = "storeDelete", group = Group.Store, message = "Deletes value from store for specified key.")
public class StoreDeleteStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Key to be deleted.
	 */
	@Param(description = "Key to be deleted.", required = true)
	private String key;

	/**
	 * Sets the key to be stored.
	 *
	 * @param key the new key to be stored
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		boolean res = context.getPersistenceStorage().remove(key);

		if(res)
		{
			exeLogger.debug("Successfully deleted entry with key: {}", key);
			return;
		}

		exeLogger.debug("No entry found with key: {}. Ignoring delete request.", key);
	}
}
