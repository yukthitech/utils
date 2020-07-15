package com.yukthitech.autox.test.ui.steps;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiClickAndDownload", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Clicks the specified target and download the result file. If no  file is downloaded, this will throw exception.")
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
	 * Time to wait for download to complete in millis. Default: 20000.
	 */
	@Param(description = "Time to wait for download to complete in millis. Default: 30000")
	private long downloadWaitTime = 30000;
	
	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 5", required = false)
	private int retryCount = 5;
	
	/**
	 * Time gap between retries.
	 */
	@Param(description = "Time gap between retries. Default: 1000", required = false)
	private int retryTimeGapMillis = IAutomationConstants.ONE_SECOND;

	/**
	 * Sets the number of retries to happen. Default: 5.
	 *
	 * @param retryCount the new number of retries to happen
	 */
	public void setRetryCount(int retryCount)
	{
		this.retryCount = retryCount;
	}
	
	/**
	 * Sets the time gap between retries.
	 *
	 * @param retryTimeGapMillis the new time gap between retries
	 */
	public void setRetryTimeGapMillis(int retryTimeGapMillis)
	{
		this.retryTimeGapMillis = retryTimeGapMillis;
	}

	/**
	 * Sets the attribute name which would be set with the downloaded file path.
	 *
	 * @param pathName the new attribute name which would be set with the downloaded file path
	 */
	public void setPathName(String pathName)
	{
		this.pathName = pathName;
	}
	
	/**
	 * Sets the time to wait for download to complete in millis. Default: 20000.
	 *
	 * @param downloadWaitTime the new time to wait for download to complete in millis
	 */
	public void setDownloadWaitTime(long downloadWaitTime)
	{
		this.downloadWaitTime = downloadWaitTime;
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
		
		exeLogger.trace("For download clicking the element specified by locator: {}", getLocatorWithParent(locator));

		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);

				if(webElement == null)
				{
					exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
					throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
				}

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
			} , retryCount, retryTimeGapMillis,
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to click element - " + getLocatorWithParent(locator)));
			
			try
			{
				Thread.sleep(downloadWaitTime);
			}catch(Exception ex)
			{}
			
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
				String path = files[0].getCanonicalPath();
				exeLogger.trace("Setting downloaded file path '{}' on context with name: {}", path, pathName);
				
				context.setAttribute(pathName, path);
			}catch(IOException ex)
			{
				throw new InvalidStateException("An error occurred while fetching cannoical path of downloaded file: " + files[0].getPath(), ex);
			}
		}catch(InvalidStateException ex)
		{
			exeLogger.error(ex, "Failed to click element - {}", getLocatorWithParent(locator));
			throw new TestCaseFailedException(this, "Failed to click element - {}", getLocatorWithParent(locator), ex);
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
		StringBuilder builder = new StringBuilder();
		builder.append("Click and Download [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}
}
