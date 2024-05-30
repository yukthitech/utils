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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.met.MethodUtils;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * Model wrapper for free marker dynamic method registration.
 * @author akiran
 */
class MethodProxy implements TemplateMethodModelEx
{
	/**
	 * Method being registered.
	 */
	private Method freeMarkerMethod;
	
	/**
	 * Method name that will be used in free marker templates.
	 */
	private String methodName;
	
	/**
	 * Instantiates a new free marker method model.
	 *
	 * @param freeMarkerMethod the free marker method
	 * @param methodName the method name
	 */
	public MethodProxy(Method freeMarkerMethod, String methodName)
	{
		this.freeMarkerMethod = freeMarkerMethod;
		this.methodName = methodName;
	}
	
	/**
	 * Fetches the types of objects specified.
	 * @param obj objects whose types needs to be fetched
	 * @return types of specified objects
	 */
	private Class<?>[] getTypes(Object obj[])
	{
		if(obj == null || obj.length == 0)
		{
			return new Class<?>[0];
		}
		
		Class<?> types[] = new Class<?>[obj.length];
		
		
		for(int i = 0; i < obj.length; i++)
		{
			if(obj[i] == null)
			{
				types[i] = null;
				continue;
			}
			
			types[i] = obj[i].getClass();
		}
		
		return types;
	}

	/* (non-Javadoc)
	 * @see freemarker.template.TemplateMethodModelEx#exec(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes"})
	@Override
	public Object exec(List arguments) throws TemplateModelException
	{
		Class<?> argTypes[] = freeMarkerMethod.getParameterTypes();
		boolean isVarArgs = freeMarkerMethod.isVarArgs();

		//for var args number of arguments will be equal to number of declared parameters in method
		// 	last + extra param will be clubbed into single array for varargs
		Object methodArgs[] = new Object[argTypes.length];
		
		arguments = (arguments == null) ? Collections.emptyList() : arguments;
		int argsSize = arguments.size();
		
		if(argTypes.length > 0)
		{
			int stdArgCount = isVarArgs ? argTypes.length - 1 : argTypes.length;
			
			for(int i = 0; i < stdArgCount; i++)
			{
				Object argVal = argsSize <= i ? CommonUtils.getDefaultValue(argTypes[i]) : arguments.get(i);
				methodArgs[i] = MethodUtils.convertArgument(argVal, argTypes[i]);
			}
			
			if(isVarArgs && argsSize >= argTypes.length)
			{
				Class<?> varArgType = argTypes[argTypes.length - 1].getComponentType();
				Object varArgs = Array.newInstance(varArgType, argsSize - stdArgCount); 
				
				for(int i = stdArgCount, j = 0; i < argsSize; i++, j++)
				{
					Array.set( varArgs, j, MethodUtils.convertArgument(arguments.get(i), varArgType) );
				}
				
				methodArgs[stdArgCount] = varArgs;
			}
		}

		try
		{
			return freeMarkerMethod.invoke(null, methodArgs);
		}catch(Exception ex)
		{
			Throwable err = ex;
			
			if(ex instanceof InvocationTargetException)
			{
				err = (Exception) ex.getCause();
			}
			
			String argTypesStr = Arrays.asList(freeMarkerMethod.getParameterTypes())
					.stream()
					.map(typ -> typ.getName())
					.collect(Collectors.joining(", "));
			
			throw new InvalidStateException("An error occurred while invoking method '{}'. "
					+ "\nJava Method: {}.{}({})"
					+ "\nArguments used: {}"
					+ "\nUsed arguments type: {}",
					methodName, 
					freeMarkerMethod.getDeclaringClass().getName(), freeMarkerMethod.getName(), argTypesStr,
					Arrays.toString( methodArgs ), Arrays.toString( getTypes(methodArgs) ), err);
		}
	}
}
