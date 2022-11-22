package com.yukthitech.autox.common;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.jexpr.JsonExprEngine;

/**
 * Constants that will be used across the classes.
 * @author akiran
 */
public interface IAutomationConstants
{
	/**
	 * Used to specify if test suites should be executed parallely or not.
	 * Overridable prop. 
	 */
	public String AUTOX_PROP_PARALLEL_POOL_SIZE = "autox.parallelExecution.poolSize";
	
	/**
	 * Used to specify if test suites can be executed parallelly or not.
	 */
	public String AUTOX_PROP_PARALLEL_EXEC_ENABLED = "autox.testSuites.parallelExecutionEnabled";
	
	public String PROP_LOG_MAX_PROP_LEN = "autox.log.max.param.len";
	
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
	
	public Pattern KEY_VALUE_PATTERN = Pattern.compile("\\s*(?<key>\\w+)\\s*\\=\\s*(?<value>.+)\\s*");

	public int TWO_SECONDS = 2;
	
	/**
	 * Used to process json expressions.
	 */
	public JsonExprEngine JSON_EXPR_ENGINE = new JsonExprEngine()
	{
		{
			super.setFreeMarkerEngine(FreeMarkerMethodManager.getFreeMarkerEngine());
		}
	};

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
	
	public String LOGS_FOLDER_NAME = "logs";
}
