package com.yukthitech.autox.test.ui.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.yukthitech.autox.AutomationContext;

/**
 * Field accessor for checkboxes and radio buttons.
 */
public class CheckboxFieldAccessor implements IFieldAccessor
{
	
	/** 
	 * The value. 
	 **/
	private static String VALUE = "value";
	
	/**
	 * Fetches check-boxes or similar elements from specified parent element
	 * with same name.
	 *
	 * @param element
	 *            Element whose groups needs to be fetched
	 * @return the list of group elementss
	 */
	private List<WebElement> findGroupedElements(WebElement element)
	{
		WebElement parentElement = element.findElement(By.xpath(".."));
		return parentElement.findElements(By.name(element.getAttribute("name")));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.
	 * selenium.WebElement)
	 */
	@Override
	public String getValue(AutomationContext context, WebElement element)
	{
		List<WebElement> elements = findGroupedElements(element);
		StringBuilder builder = new StringBuilder();

		for(WebElement welem : elements)
		{
			if(welem.isSelected())
			{
				continue;
			}

			if(builder.length() > 0)
			{
				builder.append(",");
			}

			builder.append(welem.getAttribute(VALUE));
		}

		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.
	 * selenium.WebElement, java.lang.String)
	 */
	@SuppressWarnings({ "rawtypes"})
	@Override
	public void setValue(AutomationContext context, List<WebElement> webElements, Object value)
	{
		Set<String> valueSet = new HashSet<>();

		if(value instanceof Collection)
		{
			for(Object obj : ((Collection) value))
			{
				valueSet.add("" + obj);
			}
		}
		else
		{
			valueSet.add("" + value);
		}

		for(WebElement webElem : webElements)
		{
			if(valueSet.contains(webElem.getAttribute(VALUE)))
			{
				if(!webElem.isSelected())
				{
					webElem.click();
				}
			}
			else
			{
				if(webElem.isSelected())
				{
					webElem.click();
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.
	 * selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(AutomationContext context, WebElement element)
	{
		List<WebElement> webElements = findGroupedElements(element);
		List<FieldOption> options = new ArrayList<>(webElements.size());

		for(WebElement elem : webElements)
		{
			options.add(new FieldOption(elem.getAttribute(VALUE), null));
		}

		return options;
	}
}
