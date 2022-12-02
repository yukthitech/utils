package com.yukthitech.jexpr;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;

import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class Conversions
{
	
	/**
	 * Expression used by value string which has to be replaced with resultant value.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("^\\@([\\w\\-]+)\\s*\\:\\s*(.*)$");
	
	/**
	 * In a map if this key is specified with expression, along with @value/@falseValue then the result will be result of this expression.
	 * Current value will be available in this expressions as thisValue.
	 */
	private static final String TRANSFORM = "@transform";

	/**
	 * Free marker expression type.
	 */
	private static final String EXPR_TYPE_FMARKER = "fmarker";
	
	/**
	 * Xpath expression type.
	 */
	private static final String EXPR_TYPE_XPATH = "xpath";

	/**
	 * Xpath expression type with multiple value.
	 */
	private static final String EXPR_TYPE_XPATH_MULTI = "xpathMulti";

	/**
	 * Use to load resource value.
	 */
	private static final String RES = "@resource";

	/**
	 * Use to specify that expressions in resource being loaded should be processed or not.
	 * By default this will be true.
	 */
	private static final String RES_PARAM_EXPR = "@expressions";

	/**
	 * If specified, will be added to current context as 'resParams', which can be accessed within expressions of resource.
	 */
	private static final String RES_PARAM_PARAMS = "@resParams";
	
	/**
	 * Free marker engine for expression processing.
	 */
	private FreeMarkerEngine freeMarkerEngine;
	
	public Conversions(FreeMarkerEngine freeMarkerEngine)
	{
		this.freeMarkerEngine = freeMarkerEngine;
	}

	/**
	 * If resource tag is specified in the map, this will fetch resource content from specified resource.
	 *
	 * @param map
	 *            the map
	 * @param context
	 *            the context
	 * @param path
	 *            the path
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	public boolean processMapRes(Map<String, Object> map, Map<String, Object> context, String path, ObjectWrapper<Object> value)
	{
		Object valueExpr = map.get(RES);
		
		//if value is not present, remove it
		if(valueExpr == null)
		{
			map.remove(RES);
			return false;
		}

		Object resPath = map.remove(RES);
		String content = null;
		
		try
		{
			content = IOUtils.resourceToString(resPath.toString(), Charset.defaultCharset());
		}catch(Exception ex)
		{
			throw new JsonExpressionException(path, "Failed to fail resource: {}", resPath.toString(), ex);			
		}
		
		boolean disableExpressions = "false".equalsIgnoreCase("" + map.get(RES_PARAM_EXPR));
		Object resParams = map.get(RES_PARAM_PARAMS);
		
		if(!disableExpressions)
		{
			if(resParams != null)
			{
				context.put("resParams", resParams);
			}
			
			content = freeMarkerEngine.processTemplate("res-content", content, context);
		}
		
		Object finalRes = checkForTransform(map, content, context, path);
		value.setValue(finalRes);
		return true;
	}
	
	/**
	 * If input string contains expression syntax the same will be processed and result will be returned. If not string will
	 * be evaluated as simple free marker template.
	 * @param str string to evaluate
	 * @param context context to be used
	 * @param path path where this string is found
	 * @return processed value
	 */
	public Object processString(String str, Map<String, Object> context, String path)
	{
		Matcher matcher = EXPR_PATTERN.matcher(str);
		
		if(!matcher.matches())
		{
			return freeMarkerEngine.processTemplate("jel-template", str, context);
		}
		
		String exprType = matcher.group(1);
		String expr = matcher.group(2);
		
		try
		{
			if(EXPR_TYPE_FMARKER.matches(exprType))
			{
				return freeMarkerEngine.fetchValue("jel-expr", expr, context);
			}
			else if(EXPR_TYPE_XPATH.matches(exprType))
			{
				return JXPathContext.newContext(context).getValue(expr);
			}
			else if(EXPR_TYPE_XPATH_MULTI.matches(exprType))
			{
				return JXPathContext.newContext(context).selectNodes(expr);
			}
		} catch(Exception ex)
		{
			throw new JsonExpressionException(path, "An error occurred while processing expression: %s", str, ex);
		}
		
		throw new JsonExpressionException(path, "Invalid expression type specified '%s' in expression: %s", exprType, str);
	}

	public Object checkForTransform(Map<String, Object> map, Object curValue, Map<String, Object> context, String path)
	{
		Object transformExpr = map.get(TRANSFORM);
		
		if(!(transformExpr instanceof String))
		{
			return curValue;
		}
				
		try
		{
			context.put("thisValue", curValue);
			
			return processString((String) transformExpr, context, path);
		}catch(Exception ex)
		{
			throw new JsonExpressionException(path, "An error occurred while transforming result value", ex);	
		}

	}
}
