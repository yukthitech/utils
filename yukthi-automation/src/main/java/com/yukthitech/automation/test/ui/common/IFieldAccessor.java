package com.yukthitech.automation.test.ui.common;

import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * Abstract type for accessing field data information.
 * @author akiran
 */
public interface IFieldAccessor
{
	/**
	 * Fetches value of the specified field.
	 * @param element Element from which value needs to be fetched.
	 * @return Value of the element
	 */
	public String getValue(WebElement element);
	
	/**
	 * Sets the specified value on specified element.
	 * @param element Element on which value needs to be set.
	 * @param value Value to set.
	 */
	public void setValue(WebElement element, String value);
	
	/**
	 * Fetches options from specified element.
	 * @param element Element from which options needs to be fetched.
	 * @return field options
	 */
	public List<FieldOption> getOptions(WebElement element);
}
