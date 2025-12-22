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

import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.TemplateProcessingException;

public class ExpressionUtil
{
	public static String processTemplate(FreeMarkerEngine freeMarkerEngine, String path, String name, String template, Object context)
	{
		try
		{
			return freeMarkerEngine.processTemplate(name, template, context);
		} catch(Exception ex)
		{
			String prcessingError = null;
			
			if(ex instanceof TemplateProcessingException)
			{
				prcessingError = ex.getCause().getMessage();
				ex = null;
			}
			
			throw new TransformException(path, "An error occurred while evaluating template specified at path: {}\nProcessing Error: {}\nTemplate: {}", 
					name, path, prcessingError, template, ex);	
		}
	}

	public static Object processValueExpression(FreeMarkerEngine freeMarkerEngine, String path, String name, String expression, Object context)
	{
		try
		{
			return freeMarkerEngine.fetchValue(name, expression, context);
		} catch(Exception ex)
		{
			String prcessingError = null;
			
			if(ex instanceof TemplateProcessingException)
			{
				prcessingError = ex.getCause().getMessage();
				ex = (Exception) ex.getCause();
			}
			
			throw new TransformException(path, "Invalid expression '{}' (Name: {}) specified at path: {}\nProcessing Error: {}", 
					expression, name, path, prcessingError, ex);	
		}
	}
	
	public static boolean evaluateCondition(FreeMarkerEngine freeMarkerEngine, String path, String name, String condition, Object context)
	{
		try
		{
			return freeMarkerEngine.evaluateCondition(name, condition, context);
		} catch(Exception ex)
		{
			String prcessingError = null;
			
			if(ex instanceof TemplateProcessingException)
			{
				prcessingError = ex.getCause().getMessage();
				ex = null;
			}
			
			throw new TransformException(path, "Invalid condition '{}' (Name: {}) specified at path: {}\nProcessing Error: {}", 
					condition, name, path, prcessingError, ex);	
		}
	}

}
