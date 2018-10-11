package com.yukthitech.autox.ide.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormattingRule
{
	private Pattern pattern;
	
	private IndentAction indentAction;
	
	private Pattern excludePattern;

	public FormattingRule(String pattern, IndentAction indentAction)
	{
		this(pattern, indentAction, null);
	}
	
	public FormattingRule(String pattern, IndentAction indentAction, String excludePattern)
	{
		this.pattern = Pattern.compile(pattern);
		this.indentAction = indentAction;
		
		if(excludePattern != null)
		{
			this.excludePattern = Pattern.compile(excludePattern);
		}
	}

	public IndentAction getIndentAction()
	{
		return indentAction;
	}
	
	public boolean isMatching(String line)
	{
		Matcher matcher = pattern.matcher(line);
		boolean matched = matcher.find();
		
		if(excludePattern == null)
		{
			return matched;
		}
		
		if(!matched)
		{
			return false;
		}
		
		matcher = excludePattern.matcher(line);
		return matcher.find();
	}
	
}
