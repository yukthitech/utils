package com.yukthitech.transform;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ITransformConstants
{
	/**
	 * Object mapper for parsing and formatting json.
	 */
	ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * Free marker expression type.
	 */
	String EXPR_TYPE_FMARKER = "fmarker";
	
	/**
	 * Xpath expression type.
	 */
	String EXPR_TYPE_XPATH = "xpath";

	/**
	 * Xpath expression type with multiple value.
	 */
	String EXPR_TYPE_XPATH_MULTI = "xpathMulti";
	
	public static String toPrettyJson(Object value)
	{
		try
		{
			return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		}catch(Exception ex)
		{
			throw new IllegalStateException(ex);
		}
	}
}
