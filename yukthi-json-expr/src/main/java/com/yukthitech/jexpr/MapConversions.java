package com.yukthitech.jexpr;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class MapConversions
{
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
	 * If specified, mutiple whitespaces will be replaced with single space.
	 */
	private static final String RES_PARAM_REM_WHITESPACES = "@remove-ws";

	/**
	 * If specified, will be added to current context as 'resParams', which can be accessed within expressions of resource.
	 */
	private static final String RES_PARAM_PARAMS = "@resParams";
	
	/**
	 * Free marker engine for expression processing.
	 */
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	public MapConversions(FreeMarkerEngine freeMarkerEngine)
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
		boolean removeWhitespaces = "true".equalsIgnoreCase("" + map.get(RES_PARAM_REM_WHITESPACES));
		
		if(!disableExpressions)
		{
			if(resParams != null)
			{
				context.put("resParams", resParams);
			}
			
			content = freeMarkerEngine.processTemplate("res-content", content, context);
		}
		
		if(removeWhitespaces)
		{
			content = content.replaceAll("\\s+", " ");
		}

		value.setValue(content);
		return true;
	}

	public boolean processMapJson(Map<String, Object> map, Map<String, Object> context, String path, ObjectWrapper<Object> value)
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
		boolean removeWhitespaces = "true".equalsIgnoreCase("" + map.get(RES_PARAM_REM_WHITESPACES));
		
		if(!disableExpressions)
		{
			if(resParams != null)
			{
				context.put("resParams", resParams);
			}
			
			content = freeMarkerEngine.processTemplate("res-content", content, context);
		}
		
		if(removeWhitespaces)
		{
			content = content.replaceAll("\\s+", " ");
		}

		value.setValue(content);
		return true;
	}
}
