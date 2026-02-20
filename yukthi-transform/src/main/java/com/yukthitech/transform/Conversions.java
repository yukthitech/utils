/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.transform;

import java.util.Map;
import java.util.regex.Pattern;

import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.Include;
import com.yukthitech.transform.template.TransformTemplate.Resource;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class Conversions
{
	
	/**
	 * Expression used by value string which has to be replaced with resultant value.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("^\\@([\\w\\-]+)\\s*\\:\\s*(.*)$");
	
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
	 * @param transformObject
	 *            the transform object
	 * @param context
	 *            the context
	 * @param value
	 *            the value
	 * @return true, if successful
	 */
	public boolean processMapRes(TransformObject transformObject, ITransformContext context, ObjectWrapper<Object> value, TransformEngine curEngine, TransformState transformState)
	{
		Resource resource = transformObject.getResource();

		if(resource == null)
		{
			return false;
		}
		
		String content = resource.getContent();

		String path = transformObject.getPath();
		boolean disableExpressions = resource.isExpressionsDisabled();
		Object resParams = resource.getResParams();
		
		if(!disableExpressions)
		{
			if(resParams != null)
			{
				context.setValue("resParams", resParams);
			}
			
			content = ExpressionUtil.processTemplate(freeMarkerEngine, path, "res-content", content, context);
		}
		
		Object finalRes = checkForTransform(transformObject, content, context, transformState);
		value.setValue(finalRes);
		return true;
	}
	

	/**
	 * If include tag(s) is specified in the map, this will process the specified file/resource and use the result 
	 * as current map replacement.
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
	@SuppressWarnings("unchecked")
	public boolean processInclude(TransformObject transformObject, ITransformContext context, ObjectWrapper<Object> value, TransformEngine curEngine, TransformState transformState)
	{
		Include include = transformObject.getInclude();

		if(include == null)
		{
			return false;
		}

		TransformObject paramsObj = include.getParams();
		Map<String, Object> paramsMap = null;
		
		//process expression in params map
		if(paramsObj != null)
		{
			Object processRes = curEngine.processObject(paramsObj, context, transformState.forDynField("params"));
			paramsMap = (Map<String, Object>) transformState.toSimpleObject(processRes);
		}
		
		TransformTemplate content = include.getContent();
		
		try
		{
			Object res = curEngine.process(content, new MapExprContext(context, "params", paramsMap));
			res = transformState.convertIncluded(res);
			value.setValue(res);
		}catch(Exception ex)
		{
			throw new TransformException(transformState.getPath(), "An error occurred while processing include template: {}", include.getPath(), ex);
		}

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
	public Object processExpression(Expression expression, ITransformContext context, TransformState transformState)
	{
		InternalExpressionContext.push(freeMarkerEngine, context, transformState);

		try
		{
			return TransformUtils.processExpression(freeMarkerEngine, expression, context, transformState);
		} finally
		{
			InternalExpressionContext.pop();
		}
	}
	
	public String processTemplate(String expression, ITransformContext context, String path)
	{
		try
		{
			return ExpressionUtil.processTemplate(freeMarkerEngine, path, "transform-template", expression, context);
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(path, "An error occurred while processing template: {}", expression, ex);
		}
	}

	public Object checkForTransform(TransformObject transformObject, Object curValue, ITransformContext context, TransformState transformState)
	{
		Expression transformExpr = transformObject.getTransformExpression();

		if(transformExpr == null)
		{
			return curValue;
		}
		
		try
		{
			context.setValue("thisValue", curValue);
			
			return processExpression(transformExpr, context, transformState);
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(transformState.getPath(), "An error occurred while transforming result value", ex);	
		}

	}
}
