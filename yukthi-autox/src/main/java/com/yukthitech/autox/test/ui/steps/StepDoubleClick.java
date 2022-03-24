package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

@Executable(name = "uiDblClick", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Double Clicks the specified target")
public class StepDoubleClick extends AbstractPostCheckStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Locator of the element to be triggered..
	 */
	@Param(description = "Locator of the element to be double-cicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 5", required = false)
	private int retryCount = 10;
	
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
	 * Sets the locator of the element to be triggered..
	 *
	 * @param locator the new locator of the element to be triggered
	 */
	public void setLocator(String locator)
	{
		this.locator = locator;
	}
	
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception 
	{
		exeLogger.trace("Double-Clicking the element specified by locator: {}", locator);
		
		try
		{
			ObjectWrapper<Boolean> clickedAtleastOnce = new ObjectWrapper<>(false);
			
			UiAutomationUtils.validateWithWait(() -> 
			{
				WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);
				
				if(webElement == null)
				{
					exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
					throw new NullPointerException("Failed to find element with locator: " + getLocatorWithParent(locator));
				}

				if(clickedAtleastOnce.getValue())
				{
					if(super.isPostCheckAvailable() && doPostCheck(context, exeLogger, "Before Re-dbl-click"))
					{
						exeLogger.trace("Before re-click as post check is successful, skipping re-dbl-click");
						return true;
					}
				}

				try
				{
					exeLogger.trace("Trying to double-click element specified by locator: {}", locator);

					SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
					WebDriver driver = seleniumConfiguration.getWebDriver();

					Actions actions = new Actions(driver);
					
					actions.doubleClick(webElement).perform();
					
					return doPostCheck(context, exeLogger, "Post Dbl-click");
				} catch(RuntimeException ex)
				{
					exeLogger.debug("IGNORED: An error occurred while dbl-clicking locator - {}. Error: {}", locator,  "" + ex);
					
					if(UiAutomationUtils.isElementNotAvailableException(ex))
					{
						return false;
					}
	
					throw ex;
				}
			} , retryCount, retryTimeGapMillis,
					"Waiting for element to be clickable: " + getLocatorWithParent(locator), 
					new InvalidStateException("Failed to click element - " + getLocatorWithParent(locator)));
		}catch(InvalidStateException ex)
		{
			exeLogger.error(ex, "Failed to click element - {}", getLocatorWithParent(locator));
			throw new TestCaseFailedException(this, "Failed to click element - {}", getLocatorWithParent(locator), ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Double Click [");

		builder.append("Locator: ").append(locator);

		builder.append("]");
		return builder.toString();
	}

}
