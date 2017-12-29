package com.yukthitech.autox.test.ui.steps;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Drag and drop the web elements.
 * 
 * @author Pritam.
 */
@Executable(name = {"ui_dragAndDrop", "dragAndDrop"}, requiredPluginTypes = SeleniumPlugin.class, message = "Drags the specified element to specified target")
public class DragAndDropStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Source html element to be dragged.
	 */
	@Param(description = "Locator of element which needs to be dragged")
	private String source;

	/**
	 * Destination html element area to drop.
	 */
	@Param(description = "Locator of element on which source element should be dropped")
	private String destination;

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger logger)
	{
		logger.debug(this, "Dragging element '{}' to element - {}", source, destination);
		
		WebElement sourceElement = UiAutomationUtils.findElement(context, null, source);
		WebElement destinationElement = UiAutomationUtils.findElement(context, null, destination);

		dragAndDrop(context, sourceElement, destinationElement, logger);
		
		return true;
	}

	/**
	 * Drag and drop web element.
	 * 
	 * @param sourceElement
	 *            the element to be dragged.
	 * @param destinationElement
	 *            area to be dropped.
	 */
	private void dragAndDrop(AutomationContext context, WebElement sourceElement, WebElement destinationElement, ExecutionLogger logger)
	{
		if(!sourceElement.isDisplayed())
		{
			logger.error(this, "Failed to find source element to be dragged. Locator: {}", source);
			
			throw new TestCaseFailedException("Failed to find drag element - '{}'", source);
		}

		if(!destinationElement.isDisplayed())
		{
			logger.error(this, "Failed to find targer element to be dropped. Locator: {}", destination);
			
			throw new TestCaseFailedException("Failed to find drop area element - '{}'", destination);
		}

		try
		{
			SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
			seleniumConfiguration.getWebDriver().manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
			
			Thread.sleep(2000);
			
			Actions actions = new Actions(seleniumConfiguration.getWebDriver());

			//actions.dragAndDrop(sourceElement, destinationElement).build().perform();
			
			Action dragAndDrop = actions.clickAndHold(sourceElement)
					   .moveToElement(destinationElement, 2, 2)
					   .release()
					   .build();

			dragAndDrop.perform();
		} catch(StaleElementReferenceException ex)
		{
			logger.error(this, ex, "Element with {} or {} is not attached to the page document", sourceElement, destinationElement);
			throw new TestCaseFailedException("Element with {} or {} is not attached to the page document", sourceElement, destinationElement, ex);
		} catch(NoSuchElementException e)
		{
			logger.error(this, e, "Element with {} or {} was not found in DOM ", sourceElement, destinationElement);
			throw new TestCaseFailedException("Element with {} or {} was not found in DOM ", sourceElement, destinationElement, e);
		} catch(Exception e)
		{
			logger.error(this, e, "Error occurred while performing drag and drop operation");
			throw new TestCaseFailedException("Error occurred while performing drag and drop operation ", e);
		}
	}

	/**
	 * Gets source html element to be dragged.
	 * 
	 * @return source html element to be dragged.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source html element to be dragged.
	 * 
	 * @param source
	 *            the source html element.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Gets the destination drop area html element.
	 * 
	 * @return the destination drop area html element.
	 */
	public String getDestination()
	{
		return destination;
	}

	/**
	 * Sets the destination drop area html element.
	 * 
	 * @param destination
	 *            the new destination drop area html element.
	 */
	public void setDestination(String destination)
	{
		this.destination = destination;
	}
}
