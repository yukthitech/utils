package com.yukthitech.autox.common;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Constants that will be used across the classes.
 * @author akiran
 */
public interface IAutomationConstants
{
	/**
	 * Object mapper for parsing and formatting json.
	 */
	public ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/**
	 * Pattern used to refer property in the source.
	 */
	public Pattern REF_PATTERN = Pattern.compile("ref\\s*\\:\\s*(.+)");
	
	public Pattern EXPRESSION_PATTERN = Pattern.compile("^\\s*(?<exprType>\\w+)\\s*\\:\\s*");
	
	public Pattern EXPRESSION_WITH_TYPE_PATTERN = Pattern.compile("^\\s*(?<exprType>\\w+)\\s*\\(\\s*(?<type>[\\w\\.\\<\\>\\,\\ ]+)\\s*\\)\\s*\\:\\s*");

	/**
	 * Five seconds.
	 */
	public int FIVE_SECONDS = 5;

	/**
	 * One seconds.
	 */
	public int ONE_SECOND = 1;
}
