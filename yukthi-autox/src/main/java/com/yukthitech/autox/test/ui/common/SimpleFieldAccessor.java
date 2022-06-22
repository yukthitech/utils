package com.yukthitech.autox.test.ui.common;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Accessor to access value of simple field types like - TEXT, Text area, int, etc.
 */
public class SimpleFieldAccessor implements IFieldAccessor
{
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.selenium.WebElement)
	 */
	@Override
	public String getValue(AutomationContext context, WebElement element)
	{
		if("input".equals(element.getTagName().toLowerCase()) || "textarea".equals(element.getTagName().toLowerCase()))
		{
			return element.getAttribute("value").trim();
		}
		
		return element.getText().trim();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.selenium.WebElement, java.lang.String)
	 */
	@Override
	public void setValue(AutomationContext context, WebElement element, Object value)
	{
		try
		{
			element.clear();
		}catch(Exception ex)
		{
			context.getExecutionLogger().debug("Ignoring error while trying to clear the field. Error: {}", "" + ex);
		}
		
		try
		{
			element.sendKeys("" + value);
		}catch(Exception ex)
		{
			context.getExecutionLogger().debug("Failed to set the field value using sendKeys(). Trying to set the value using JS attrbute. Error: %s", ex);
			
			SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
			WebDriver driver = seleniumConfiguration.getWebDriver();
			
			try
			{
				((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", 
		                element, "value", "" + value);
			}catch(Exception ex1)
			{
				context.getExecutionLogger().debug("Failed to set the field value using JS set attribute way also. Throwing back the actual exception", ex1);
				throw ex;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(AutomationContext context, WebElement element)
	{
		return null;
	}
}
