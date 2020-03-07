package com.yukthitech.autox.test.ui.steps;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Helps in switching between windows.
 * 
 * @author akiran
 */
@Executable(name = "uiSwitchWindow", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Helps in switching between windows")
public class SwitchWindow extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Locator of the window. If none is specified, main window will be selected..
	 */
	@Param(description = "Locator of the window. If none is specified, main window will be selected.", required = false)
	private String locator;
	
	@Param(description = "If no locator is specified and this flag is true, switch would be done to newly opened window. "
			+ "New window is determined based on previous SyncWindowHandles invocation (or window other than main window). "
			+ "If no new window is found, an exception would be thrown. Default: false", required = false)
	private boolean newWindow = false;
	
	/**
	 * Compares the current open window handles with last sync window handles. And returns new window handle if any.
	 * @param seleniumPlugin plugin to use to fetch context window handles and all window handles
	 * @return newly opened window handle
	 */
	private String getNewWindowHandle(SeleniumPlugin seleniumPlugin)
	{
		Set<String> contextHandles = seleniumPlugin.getWindowHandles();
		
		WebDriver driver = seleniumPlugin.getWebDriver();
		Set<String> newHandles = driver.getWindowHandles();
		
		if(newHandles == null)
		{
			throw new InvalidStateException("No open windows found on current context");
		}
		
		newHandles.removeAll(contextHandles);
		
		if(newHandles.isEmpty())
		{
			throw new InvalidStateException("No new windows found on current context");
		}
		
		if(newHandles.size() > 1)
		{
			throw new InvalidStateException("Multiple new windows found on current context");
		}
		
		return newHandles.iterator().next();
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Switching to window: {}", locator);

		SeleniumPlugin seleniumPlugin = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumPlugin.getWebDriver();
		
		if(StringUtils.isBlank(locator))
		{
			if(newWindow)
			{
				exeLogger.debug("Trying to switch to new window");
				locator = getNewWindowHandle(seleniumPlugin);
			}
			else
			{
				exeLogger.debug("As no locator is specified switching to main window");
				locator = seleniumPlugin.getMainWindowHandle();
			}
		}

		driver.switchTo().window(locator);
		
		return true;
	}

	/**
	 * Sets the locator for button.
	 *
	 * @param locator
	 *            the new locator for button
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}
	
	/**
	 * Sets the new window.
	 *
	 * @param newWindow the new new window
	 */
	public void setNewWindow(boolean newWindow)
	{
		this.newWindow = newWindow;
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
		builder.append("[");

		builder.append("Locator: ").append(locator);
		builder.append(", New Window: ").append(newWindow);

		builder.append("]");
		return builder.toString();
	}
}
