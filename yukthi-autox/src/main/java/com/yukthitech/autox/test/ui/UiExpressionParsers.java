package com.yukthitech.autox.test.ui;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.expr.ExpressionParser;
import com.yukthitech.autox.expr.IPropertyPath;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;

/**
 * Ui related expression parsers.
 * @author akiran
 */
public class UiExpressionParsers
{
	@ExpressionParser(type = "uival", description = "Parses provided exprssion as ui locator and fetches/sets its value.", example = "uival: id:name")
	public Object propertyParser(AutomationContext context, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				UiAutomationUtils.populateField(context, (WebElement) null, expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				WebElement element = UiAutomationUtils.findElement(context, (String) null, expression);
				String value = null;
				
				if("input".equals(element.getTagName().toLowerCase()))
				{
					value = element.getAttribute("value").trim();
				}
				else
				{
					value = element.getText().trim();
				}
				
				return value;
			}
		};
	}

}
