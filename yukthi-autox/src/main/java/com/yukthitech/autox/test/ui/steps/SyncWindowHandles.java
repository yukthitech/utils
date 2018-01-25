package com.yukthitech.autox.test.ui.steps;

import java.util.Set;

import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Syncs the current open window handles to context. Which can be used to identify 
 * 
 * @author akiran
 */
@Executable(name = {"uiSyncWindowHandles"}, requiredPluginTypes = SeleniumPlugin.class, message = "Syncs the current open window handles to context. Which can be used to identify ")
public class SyncWindowHandles extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace(this, "Synchronizig window handles");

		SeleniumPlugin seleniumPlugin = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumPlugin.getWebDriver();

		Set<String> windowHandles = driver.getWindowHandles();
		
		exeLogger.debug(this, "Got window handles as: {}", windowHandles);
		seleniumPlugin.setWindowHandles(windowHandles);
		
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