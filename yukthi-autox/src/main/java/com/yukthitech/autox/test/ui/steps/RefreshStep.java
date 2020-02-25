package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = "uiRefresh", requiredPluginTypes = SeleniumPlugin.class, message = "Refreshes the current page.")
public class RefreshStep extends AbstractPostCheckStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Refreshing the current page");

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		UiAutomationUtils.validateWithWait(() -> 
		{
			try
			{
				driver.navigate().refresh();
				
				//after refresh check the post-check and return result approp
				return doPostCheck(exeLogger, "Post Refresh");
			} catch(RuntimeException ex)
			{
				exeLogger.debug("IGNORED: An error occurred while doing refresh. Error: {}", "" + ex);
				
				throw ex;
			}
		} , IAutomationConstants.SIXTY_SECONDS, IAutomationConstants.ONE_SECOND,
				"Waiting for refresh to be successful", 
				new InvalidStateException("Failed to refresh"));
		return true;
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
		builder.append("[]");
		return builder.toString();
	}
}
