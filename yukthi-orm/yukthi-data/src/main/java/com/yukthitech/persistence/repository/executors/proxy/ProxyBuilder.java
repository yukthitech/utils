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
package com.yukthitech.persistence.repository.executors.proxy;

import java.lang.reflect.InvocationHandler;

import com.yukthitech.utils.exceptions.InvalidStateException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class ProxyBuilder
{
	public static Object buildProxy(Class<?> baseType, Class<?> interfaceType, InvocationHandler handler)
	{
		DynamicType.Builder<?> builder = new ByteBuddy()
			.subclass(baseType);
		
		if(interfaceType != null)
		{
			builder = builder.implement(interfaceType);
		}
	
		Class<?> cls = builder
			.method(ElementMatchers.any()).intercept(InvocationHandlerAdapter.of(handler))
			.make()
			.load(baseType.getClassLoader())
			.getLoaded();
		
		try
		{
			return cls.getConstructor().newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating instance of dynamic type", ex);
		}
	}
}
