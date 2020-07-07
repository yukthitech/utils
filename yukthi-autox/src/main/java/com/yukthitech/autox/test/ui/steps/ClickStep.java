package com.yukthitech.autox.test.ui.steps;

import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
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
@Executable(name = "uiClick", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Clicks the specified target")
public class ClickStep extends AbstractPostCheckStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator for button.
	 */
	@Param(description = "Locator of the element to be clicked. Out of located elements, first element will be clicked.", sourceType = SourceType.UI_LOCATOR)
	private String locator;

	
	/**
	 * Number of retries to happen. Default: 5
	 */
	@Param(description = "Number of retries to happen. Default: 10", required = false)
	private int retryCount = 10;
	
	/**
	 * Time gap between retries.
	 */
	@Param(description = "Time gap between retries. Default: 1000", required = false)
	private int retryTimeGapMillis = IAutomationConstants.ONE_SECOND;
	
	@Param(description = "Flag to enforce clicking using js instead of selenium", required = false)
	private boolean clickByJs = false;
	
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
	
	public void setClickByJs(boolean clickByJs)
	{
		this.clickByJs = clickByJs;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Clicking the element specified by locator: {}", locator);

		try
		{
			AtomicInteger clickedAtleastOnce = new AtomicInteger(0);
			
			UiAutomationUtils.validateWithWait(() -> 
			{
				try
				{
					WebElement webElement = UiAutomationUtils.findElement(context, super.parentElement, locator);

					if(webElement == null)
					{
						exeLogger.error("Failed to find element with locator: {}", getLocatorWithParent(locator));
						throw new IllegalArgumentException("Failed to find element with locator: " + getLocatorWithParent(locator));
					}

					//if at least once button is clicked
					if(clickedAtleastOnce.get() > 0)
					{
						if(super.isPostCheckAvailable() && doPostCheck(exeLogger, "Before re-click"))
						{
							exeLogger.trace("Before re-click as post check is successful, skipping re-click");
							return true;
						}
					}
					
					exeLogger.trace("Trying to click element specified by locator: {}", locator);
					
					try
					{
						if(clickByJs)
						{
							exeLogger.debug("As per config clicking the target element using js..");

							SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
							WebDriver driver = seleniumConfiguration.getWebDriver();

							((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
							return doPostCheck(exeLogger, "Post Click");
						}
						else
						{
							webElement.click();	
						}
					}catch(RuntimeException ex)
					{
						//if second click is also resulted in error, try js way of clicking.
						if(clickedAtleastOnce.get() > 0)
						{
							exeLogger.debug(
									"Click element failed with error twice or more than twice. So trying to click the element using JS. Error: {} "
									, "" + ex);
							SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
							WebDriver driver = seleniumConfiguration.getWebDriver();

							((JavascriptExecutor) driver).executeScript("arguments[0].click();", webElement);
							return doPostCheck(exeLogger, "Post Click");
						}
						
						throw ex;
					}
					
					clickedAtleastOnce.incrementAndGet();
					
					//after click check the post-check and return result approp
					return doPostCheck(exeLogger, "Post Click");
				} catch(RuntimeException ex)
				{
					exeLogger.debug("IGNORED: An error occurred while clicking locator - {}. Error: {}", locator,  "" + ex);
					
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
