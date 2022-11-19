package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "uiCloseSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Closes the current browser window.")
public class CloseSessionStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * To be set to true, if the driver needs to be reset. So that browser can be opened freshly.
	 */
	@Param(description = "To be set to true, if the driver needs to be reset. So that browser can be opened freshly. Default: false")
	private boolean resetDriver;
	
	public void setResetDriver(boolean resetDriver)
	{
		this.resetDriver = resetDriver;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		
		seleniumSession.getWebDriver(driverName).close();
		//seleniumConfiguration.getWebDriver().close();
		
		if(resetDriver)
		{
			exeLogger.debug("Waiting for 2 Secs, for current session to close completely before resetting driver");
			
			AutomationUtils.sleep(2000);
		
			seleniumSession.getWebDriver(driverName).quit();
			seleniumSession.resetDriver(driverName);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Close Session");
		return builder.toString();
	}

}
