package com.yukthitech.autox.common;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Constants that will be used across the classes.
 * @author akiran
 */
public interface IAutomationConstants
{
	public String STEP_NAME_SPACE = "http://autox.yukthitech.com/steps";
	
	public String FUNC_NAME_SPACE = "http://autox.yukthitech.com/functions";

	/**
	 * Object mapper for parsing and formatting json.
	 */
	public ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/**
	 * Pattern used to refer property in the source.
	 */
	public Pattern REF_PATTERN = Pattern.compile("ref\\s*\\:\\s*(.+)");
	
	public Pattern EXPRESSION_PATTERN = Pattern.compile("^\\s*(?<exprType>\\w+)\\s*\\:\\s*");
	
	public Pattern EXPRESSION_WITH_PARAMS_PATTERN = Pattern.compile("^\\s*(?<exprType>\\w+)\\s*\\(\\s*(?<params>.+)\\s*\\)\\s*\\:\\s*");
	
	public Pattern KEY_VALUE_PATTERN = Pattern.compile("\\s*(?<key>\\w+)\\s*\\=\\s*(?<value>\\w+)\\s*");

	public int TWO_SECONDS = 2;
	
	/**
	 * Five seconds.
	 */
	public int FIVE_SECONDS = 5000;
	
	public int TEN_SECONDS = 10000;


	/**
	 * Five seconds.
	 */
	public int SIXTY_SECONDS = 60000;

	/**
	 * One seconds.
	 */
	public int ONE_SECOND = 1000;
	
	public int TWO_MIN_MILLIS = 2 * 60000;
}
