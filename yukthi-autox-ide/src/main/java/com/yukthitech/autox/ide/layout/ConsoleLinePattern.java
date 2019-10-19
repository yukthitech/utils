package com.yukthitech.autox.ide.layout;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Console line pattern according to which lines in which text in console will
 * be rendered.
 * 
 * @author akiran
 */
public class ConsoleLinePattern implements Validateable
{
	/**
	 * Line pattern to be matched.
	 */
	private Pattern pattern;

	/**
	 * Color to be used when matched.
	 */
	private String color;

	public Pattern getPattern()
	{
		return pattern;
	}

	public void setPattern(String linePattern)
	{
		this.pattern = Pattern.compile(linePattern);
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(pattern == null)
		{
			throw new ValidateException("No pattern is specified.");
		}
		
		if(StringUtils.isBlank(color))
		{
			throw new ValidateException("No color is specified.");
		}
	}
}
