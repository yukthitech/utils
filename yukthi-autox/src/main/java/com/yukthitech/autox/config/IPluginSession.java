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
	 * This will be called when current thread is done with this session and releases it. In this
	 * case plugin may decide to cache the session for future usage.
	 */
	public void release();

	/**
	 * Called when session has to be fully closed.
	 */
	public default void close()
	{
	}
}
