package com.yukthitech.autox.test.ui.steps;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = {"uiClickAndDownload"}, requiredPluginTypes = SeleniumPlugin.class, message = "Clicks the specified target and download the result file. If no  file is downloaded, this will throw exception.")
public class ClickAndDownloadStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be triggered. Out of located elements, first element will be clicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;
	
	/**
	 * Attribute name which would be set with the downloaded file path.
	 */
	@Param(description = "Attribute name which would be set with the downloaded file path.")
	private String pathName;
	
	/**
	 * Sets the attribute name which would be set with the downloaded file path.
	 *
	 * @param pathName the new attribute name which would be set with the downloaded file path
	 */
	public void setPathName(String pathName)
	{
		this.pathName = pathName;
	}
	
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		SeleniumPlugin plugin = (SeleniumPlugin) context.getPlugin(SeleniumPlugin.class);
		
		if(!plugin.isDownloadsSupported())
		{
			throw new InvalidStateException("Current driver does not support download automation"); 
		}
		
		plugin.cleanDownloadFolder();
		
		exeLogger.trace(this, "Clicking the element specified by locator: {}", getLocatorWithParent(locator));

		WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);

		if(webElement == null)
		{
			exeLogger.error(this, "Failed to find element with locator: {}", getLocatorWithParent(locator));
			throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
		}

		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				try
				{
					webElement.click();
					return true;
				} catch(RuntimeException ex)
				{
					if(ex.getMessage().toLowerCase().contains("not clickable"))
					{
						return false;
					}
	
					throw ex;
				}
			} , UiAutomationUtils.FIVE_SECONDS, 
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to click element - " + getLocatorWithParent(locator)));
			
			File downloadFolder = new File(plugin.getDownloadFolder());
			File files[] = downloadFolder.listFiles();
			
			if(files == null || files.length == 0)
			{
				throw new InvalidStateException("No file found in download folder: {}", downloadFolder.getPath());
			}
			
			if(files.length > 1)
			{
				throw new InvalidStateException("Multiple files found in download folder: {}", downloadFolder.getPath());
			}
			
			try
			{
				context.setAttribute(pathName, files[0].getCanonicalPath());
			}catch(IOException ex)
			{
				throw new InvalidStateException("An error occurred while fetching cannoical path of downloaded file: " + files[0].getPath(), ex);
			}
		}catch(InvalidStateException ex)
		{
			exeLogger.error(this, ex, "Failed to click element - {}", getLocatorWithParent(locator));
			throw new TestCaseFailedException("Failed to click element - {}", getLocatorWithParent(locator), ex);
		}
		
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

		builder.append("]");
		return builder.toString();
	}
}
