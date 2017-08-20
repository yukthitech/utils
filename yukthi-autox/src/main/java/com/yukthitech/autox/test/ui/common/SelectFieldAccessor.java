package com.yukthitech.autox.test.ui.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Field accessor for select elements.
 * @author akiran
 */
public class SelectFieldAccessor implements IFieldAccessor
{
	/**
	 * Pattern to find how to populate the value.
	 */
	private static final Pattern VALUE_PATTERN = Pattern.compile("(\\w+)\\s*\\:\\s*(.+)");
	
	/**
	 * Value prefix when selection should be done based on index.
	 */
	private static final String BY_INDEX = "index";
	
	/**
	 * Value prefix when selection should be done based on label.
	 */
	private static final String BY_LABEL = "label";
	
	/**
	 * Value prefix when selection should be done based on value.
	 */
	private static final String BY_VALUE = "value";
	
	/** 
	 * The invalid message. 
	 **/
	private static String INVALID_MESSAGE = "Invalid select element specified - {}";		
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getValue(org.openqa.selenium.WebElement)
	 */
	@Override
	public String getValue(WebElement element)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		WebElement selectedOption =  new Select(element).getFirstSelectedOption();
		
		if(selectedOption == null)
		{
			return null;
		}
		
		return selectedOption.getAttribute(BY_VALUE );
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#setValue(org.openqa.selenium.WebElement, java.lang.String)
	 */
	@Override
	public void setValue(WebElement element, Object valueObj)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		String value = "" + valueObj;
		
		Select select =  new Select(element);
		Matcher matcher = VALUE_PATTERN.matcher(value);
		String type = BY_VALUE;

		if(matcher.matches())
		{
			type = matcher.group(1);
			value = matcher.group(2);
		}
		
		if(BY_INDEX.equals(type))
		{
			select.selectByIndex(Integer.parseInt(type));
		}
		else if(BY_LABEL.equals(type))
		{
			select.selectByVisibleText(value);
		}
		else
		{
			select.selectByValue(value);
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ui.automation.common.IFieldAccessor#getOptions(org.openqa.selenium.WebElement)
	 */
	@Override
	public List<FieldOption> getOptions(WebElement element)
	{
		if(element instanceof Select)
		{
			throw new InvalidArgumentException(INVALID_MESSAGE, element);
		}

		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		
		if(options == null || options.isEmpty())
		{
			return null;
		}
		
		List<FieldOption> fieldOptions = new ArrayList<>(options.size());
		
		for(WebElement optElement : options)
		{
			fieldOptions.add(new FieldOption(optElement.getAttribute(BY_VALUE ), optElement.getText()));
		}
		
		return fieldOptions;
	}
}
