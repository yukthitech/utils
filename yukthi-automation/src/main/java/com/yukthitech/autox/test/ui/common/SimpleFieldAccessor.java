package com.yukthitech.autox.test.ui.common;

import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * Accessor to access value of simple field types like - TEXT, Text area, int, etc.
 */
public class SimpleFieldAccessor implements IFieldAccessor
{
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.selenium.WebElement)
	 */
	@Override
	public String getValue(WebElement element)
	{
		return element.getText();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.selenium.WebElement, java.lang.String)
	 */
	@Override
	public void setValue(WebElement element, String value)
	{
		element.clear();
		element.sendKeys(value);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(WebElement element)
	{
		return null;
	}
}
