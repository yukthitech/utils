package com.yukthitech.autox.test.ui.steps;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Base or wrapper object to hold ui conditions.
 * @author akiran
 */
public abstract class BaseConditions extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	public static class BaseCondition
	{
		ExpectedCondition<?> condition;

		@Param(description = "Time out for this condition in seconds.")
		int timeOutInSec = 60;

		@Param(description = "Time gap between condition checks.")
		int timeGapMillis = 100;
	}

	public static class ValueCondition extends BaseCondition
	{
		@Param(description = "Value to be used.")
		String value;
	}

	public static class LocatorCondition extends BaseCondition
	{
		@Param(description = "Locator of the element on which condition to be checked.")
		String locator;
	}

	public static class LocatorValueCondition extends LocatorCondition
	{
		@Param(description = "Value to be checked.")
		String value;
	}

	public static class AttributeCondition extends LocatorCondition
	{
		@Param(description = "Name of the attribute")
		String name;
		
		@Param(description = "Value of the attribute")
		String value;
	}

	protected List<BaseCondition> conditions = new ArrayList<BaseCondition>();

	/**
	 * An expectation for checking the title of a page.
	 * 
	 * @param wrapper
	 */
	@ChildElement(description = "Condition to check page title")
	public void addTitleIs(ValueCondition wrapper)
	{
		wrapper.condition = ExpectedConditions.titleIs(wrapper.value);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking that the title contains a case-sensitive
	 * substring
	 * 
	 * @param wrapper
	 */
	public void addTitleContains(ValueCondition wrapper)
	{
		wrapper.condition = ExpectedConditions.titleContains(wrapper.value);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for the URL of the current page to contain specific text.
	 * 
	 * @param wrapper
	 */
	public void addUrlToBe(ValueCondition wrapper)
	{
		wrapper.condition = ExpectedConditions.urlToBe(wrapper.value);
		conditions.add(wrapper);
	}

	/**
	 * Expectation for the URL to match a specific regular expression
	 * 
	 * @param wrapper
	 */
	public void addUrlMatchesRegex(ValueCondition wrapper)
	{
		wrapper.condition = ExpectedConditions.urlToBe(wrapper.value);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking that an element, known to be present on the
	 * DOM of a page, is visible. Visibility means that the element is not only
	 * displayed but also has a height and width that is greater than 0.
	 * 
	 * @param wrapper
	 */
	public void addIsVisible(LocatorCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.visibilityOfAllElementsLocatedBy(locator);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking that an element is either invisible or not present on the DOM.
	 * @param wrapper
	 */
	public void addIsInvisible(LocatorCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.invisibilityOfElementLocated(locator);
		conditions.add(wrapper);
	}

	public void addIsClickable(LocatorCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.elementToBeClickable(locator);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given element is selected.
	 * @param wrapper
	 */
	public void addIsSelected(LocatorCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.elementToBeSelected(locator);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given element is not selected.
	 * @param wrapper
	 */
	public void addIsNotSelected(LocatorCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.elementSelectionStateToBe(locator, false);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given text is present in the specified
	 * element.
	 * 
	 * @param wrapper
	 */
	public void addTextPresentIn(LocatorValueCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.textToBePresentInElementLocated(locator, wrapper.value);
		conditions.add(wrapper);
	}

	/**
	 * An expectation for checking if the given text is present in the specified
	 * elements value attribute.
	 * 
	 * @param wrapper
	 */
	public void addTextPresentInElementValue(LocatorValueCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.textToBePresentInElementValue(locator, wrapper.value);
		conditions.add(wrapper);
	}
	
	public void addAttributeValueIs(AttributeCondition wrapper)
	{
		By locator = UiAutomationUtils.getLocator(wrapper.locator);
		wrapper.condition = ExpectedConditions.attributeToBe(locator, wrapper.name, wrapper.value);
		conditions.add(wrapper);
	}

	public void addAlertIsPresent(BaseCondition wrapper)
	{
		wrapper.condition = ExpectedConditions.alertIsPresent();
		conditions.add(wrapper);
	}
}
