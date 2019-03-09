package com.yukthitech.autox.test.ui.steps;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Waits for locator to be part of the page and is visible.
 * @author akiran
 */
@Executable(name = "uiWaitFor", requiredPluginTypes = SeleniumPlugin.class, message = "Waits for (at least one) specified element to become visible/hidden")
public class WaitForStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * locator to wait for.
	 */
	@Param(description = "Locator(s) of the element to be waited for", sourceType = SourceType.UI_LOCATOR)
	private List<String> locators;
	
	/**
	 * If true, this step waits for element with specified locator gets removed or hidden.
	 */
	@Param(description = "If true, this step waits for element with specified locator gets removed or hidden.\nDefault: false", required = false)
	private String hidden = "false";
	
	/**
	 * Total wait time in seconds for element to become visible or hidden. Default: 5 sec.
	 */
	@Param(description = "Total wait time in seconds for element to become visible or hidden. Default: 5 sec", required = false)
	private int waitTime = IAutomationConstants.SIXTY_SECONDS;
	
	/**
	 * Gap time in seconds to wait for between each check. Default: 1 sec.
	 */
	@Param(description = "Gap time in seconds to wait for between each check. Default: 1 sec", required = false)
	private int gapTime = IAutomationConstants.ONE_SECOND; 

	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Waiting for element '{}' to become {}", locators, "true".equals(hidden) ? "Invisible" : "Visible");
		
		try
		{
			UiAutomationUtils.validateWithWait(() -> 
			{
				for(String locator : this.locators)
				{
					WebElement element = UiAutomationUtils.findElement(context, parentElement, locator);
					
					//if element needs to be checked for invisibility
					if("true".equals(hidden))
					{
						if(element == null || !element.isDisplayed())
						{
							exeLogger.debug("Found locator '{}' to be hidden", getLocatorWithParent(locator));
							return true;
						}
					}
					//if element needs to be checked for visibility
					else if(element != null && element.isDisplayed())
					{
						exeLogger.debug("Found locator '{}' to be visible.", getLocatorWithParent(locator));
						return true;
					}
				}
				
				return false;
			}, waitTime, gapTime, "Waiting for element: " + locators, 
				new InvalidStateException("Failed to find element - " + locators));
			
		} catch(InvalidStateException ex)
		{
			exeLogger.error(ex, ex.getMessage());
			throw new TestCaseFailedException(this, ex.getMessage(), ex);
		}
		
		return true;
	}

	/**
	 * Gets the locator to wait for.
	 *
	 * @return the locator to wait for
	 */
	public List<String> getLocators()
	{
		return locators;
	}
	
	/**
	 * Sets the locator to wait for.
	 *
	 * @param locator the new locator to wait for
	 */
	public void addLocator(String locator)
	{
		if(this.locators == null)
		{
			this.locators = new ArrayList<>();
		}
		
		this.locators.add(locator);
	}
	
	/**
	 * Sets the if true, this step waits for element with specified locator gets removed or hidden.
	 *
	 * @param hidden the new if true, this step waits for element with specified locator gets removed or hidden
	 */
	public void setHidden(String hidden)
	{
		this.hidden = hidden;
	}
	
	/**
	 * Sets the total wait time in seconds for element to become visible or hidden. Default: 5 sec.
	 *
	 * @param waitTime the new total wait time in seconds for element to become visible or hidden
	 */
	public void setWaitTime(int waitTime)
	{
		this.waitTime = waitTime;
	}

	/**
	 * Sets the gap time in seconds to wait for between each check. Default: 1 sec.
	 *
	 * @param gapTime the new gap time in seconds to wait for between each check
	 */
	public void setGapTime(int gapTime)
	{
		this.gapTime = gapTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Locators: ").append(locators);
		builder.append(",").append("Hidden: ").append(hidden);
		builder.append(",").append("Wait Time: ").append(waitTime);
		builder.append(",").append("Gap Time: ").append(gapTime);

		builder.append("]");
		return builder.toString();
	}
}
