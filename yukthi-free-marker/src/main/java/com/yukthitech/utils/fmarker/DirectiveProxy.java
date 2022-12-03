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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.yukthitech.utils.exceptions.InvalidStateException;

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
	 * Instantiates a new directive proxy.
	 *
	 * @param method the method
	 */
	public DirectiveProxy(Method method)
	{
		this.method = method;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		try
		{
			method.invoke(null, env, params, loopVars, body);
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
			
			throw new InvalidStateException("An error occurred while executing directiv method: {}.{}", method.getDeclaringClass().getName(), method.getName(), e);
		} catch(Exception e)
		{
			throw new InvalidStateException("An error occurred while executing directiv method: {}.{}", method.getDeclaringClass().getName(), method.getName(), e);
		}
	}

}
