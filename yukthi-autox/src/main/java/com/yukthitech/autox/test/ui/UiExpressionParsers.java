package com.yukthitech.autox.test.ui;

import org.openqa.selenium.WebElement;

import com.yukthitech.autox.filter.ExpressionFilter;
import com.yukthitech.autox.filter.FilterContext;
import com.yukthitech.autox.filter.IPropertyPath;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Ui related expression parsers.
 * @author akiran
 */
public class UiExpressionParsers
{
	@ExpressionFilter(type = "uival", description = "Parses provided exprssion as ui locator and fetches/sets its value.", example = "uival: id:name")
	public IPropertyPath propertyParser(FilterContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				Object parent = parserContext.getCurrentValue();
				
				if(parent != null && !(parent instanceof String) && !(parent instanceof WebElement))
				{
					throw new InvalidArgumentException("Invalid/incompatible parent specified from piped input. Input: {}", parent);
				}
				
				if(parent == null)
				{
					UiAutomationUtils.populateField(parserContext.getAutomationContext(), (WebElement) null, expression, value);
				}
				else if(parent instanceof String)
				{
					UiAutomationUtils.populateField(parserContext.getAutomationContext(), (String) parent, expression, value);
				}
				else
				{
					UiAutomationUtils.populateField(parserContext.getAutomationContext(), (WebElement) parent, expression, value);
				}
			}
			
			@Override
			public Object getValue() throws Exception
			{
				Object parent = parserContext.getCurrentValue();
				
				if(parent != null && !(parent instanceof String) && !(parent instanceof WebElement))
				{
					throw new InvalidArgumentException("Invalid/incompatible parent specified from piped input. Input: {}", parent);
				}
				
				WebElement element = null;
				
				if(parent == null)
				{
					element = UiAutomationUtils.findElement(parserContext.getAutomationContext(), (String) null, expression);
				}
				else if(parent instanceof String)
				{
					element = UiAutomationUtils.findElement(parserContext.getAutomationContext(), (String) parent, expression);
				}
				else
				{
					element = UiAutomationUtils.findElement(parserContext.getAutomationContext(), (WebElement) parent, expression);
				}
				
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
