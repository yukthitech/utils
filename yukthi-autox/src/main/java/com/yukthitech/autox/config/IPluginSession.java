package com.yukthitech.autox.config;

import com.yukthitech.autox.context.AutomationContext;

/**
 * Represents a session of plugin. The framework ensures only one thread
 * have access to a session.
 * 
 * @author akranthikiran
 */
public interface IPluginSession
{
	public IPlugin<?, ?> getParentPlugin();
	
	public default void handleError(AutomationContext context, ErrorDetails errorDetails)
	{}

	/**
	 * Called when close is called on current execution context. Which generally happens before destroying
	 * current execution context.
	 */
	public default void close() throws Exception
	{
	}
}
