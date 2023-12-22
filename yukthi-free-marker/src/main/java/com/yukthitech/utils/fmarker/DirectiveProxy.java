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
package com.yukthitech.utils.fmarker;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.doc.FreeMarkerDirectiveDoc;
import com.yukthitech.utils.fmarker.doc.ParamDoc;
import com.yukthitech.utils.fmarker.met.MethodUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Proxy for invoking directive methods.
 * @author akiran
 */
class DirectiveProxy implements TemplateDirectiveModel
{
	/**
	 * Target method to be invoked.
	 */
	private Method method;
	
	/**
	 * Parameters accepted by this directive.
	 */
	private List<ParamDoc> parameters;
	
	/**
	 * Instantiates a new directive proxy.
	 *
	 * @param method the method
	 */
	public DirectiveProxy(Method method, FreeMarkerDirectiveDoc dirDoc)
	{
		this.method = method;
		this.parameters = dirDoc.getParameters();
		this.parameters = (this.parameters == null) ? Collections.emptyList() : this.parameters;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void execute(Environment env, Map paramMap, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		Object params[] = new Object[parameters.size()];
		int idx = -1;
		
		for(ParamDoc paramDoc : parameters)
		{
			idx++;
			
			if(paramDoc.isBody())
			{
				StringWriter writer = new StringWriter();
				body.render(writer);

				params[idx] = writer.toString();
			}
			else if(paramDoc.isAllParams())
			{
				Map<Object, Object> actMap = paramMap;
				Map<String, Object> allParamMap = new LinkedHashMap<>();
				Class<?> valType = Object.class;
				
				if(paramDoc.getParameter().getParameterizedType() instanceof ParameterizedType)
				{
					ParameterizedType parameterizedType = (ParameterizedType) paramDoc.getParameter().getParameterizedType();
					valType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
				}
				
				for(Map.Entry<Object, Object> entry : actMap.entrySet())
				{
					String key = MethodUtils.convertArgument(entry.getKey(), String.class);
					Object val = MethodUtils.convertArgument(entry.getValue(), valType);
					
					allParamMap.put(key, val);
				}

				params[idx] = allParamMap;
				continue;
			}
			else
			{
				params[idx] = paramMap.get(paramDoc.getName());
			}
			
			Class<?> paramType = paramDoc.getParameter().getType();
			params[idx] = (params[idx] == null) ? CommonUtils.getDefaultValue(paramType) : MethodUtils.convertArgument(params[idx], paramType);
		}
		
		try
		{
			Object result = method.invoke(null, params);
			
			if(result != null)
			{
				env.getOut().append(result.toString());
			}
		} catch(InvocationTargetException e)
		{
			Throwable ex = e.getCause();
			
			if(ex instanceof TemplateException)
			{
				throw (TemplateException) ex;
			}
			
			if(ex instanceof IOException)
			{
				throw (IOException) ex;
			}
			
			throw new InvalidStateException("An error occurred while executing directive method: {}.{}", method.getDeclaringClass().getName(), method.getName(), e);
		} catch(Exception e)
		{
			throw new InvalidStateException("An error occurred while executing directive method: {}.{}", method.getDeclaringClass().getName(), method.getName(), e);
		}
	}

}
