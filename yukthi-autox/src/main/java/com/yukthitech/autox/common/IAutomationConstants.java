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

	/**
	 * Five seconds.
	 */
	public int FIVE_SECONDS = 5;

	/**
	 * One seconds.
	 */
	public int ONE_SECOND = 1;
}
