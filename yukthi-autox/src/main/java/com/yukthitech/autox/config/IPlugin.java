package com.yukthitech.autox.config;

/**
 * Interface indicating the target object is Plugin.
 * @author akiran
 */
public interface IPlugin<AT, S extends IPluginSession>
{
	/**
	 * Fetches the argument bean type required by this configuration. Can be null. Command line arguments will be mapped
	 * to instance of this type and will be passed during initialization. 
	 * @return required argument bean
	 */
	public default Class<AT> getArgumentBeanType()
	{
		return null;
	}
	
	/**
	 * Called by framework once all configurations and test suites are loaded
	 * and before executing test suites.
	 * @param context Current automation context.
	 * @param args Mapped command line arguments bean
	 */
	public default void initialize(AT args)
	{}
	
	public S newSession();
	
	/**
	 * Called when close is called on global execution context. Which generally happens before destroying
	 * global execution context.
	 */
	public default void close() throws Exception
	{
	}
	
}
