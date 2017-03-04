package com.yukthitech.automation.config;

import com.yukthitech.automation.AutomationContext;

/**
 * Interface indicating the target object is Plugin.
 * @author akiran
 */
public interface IPlugin<AT>
{
	/**
	 * Fetches the argument bean type required by this configuration. Can be null. Command line arguments will be mapped
	 * to instance of this type and will be passed during initialization. 
	 * @return required argument bean
	 */
	public Class<AT> getArgumentBeanType();
	
	/**
	 * Called by framework once all configurations and test suites are loaded
	 * and before executing test suites.
	 * @param context Current automation context.
	 * @param args Mapped command line arguments bean
	 */
	public void initialize(AutomationContext context, AT args);
}
