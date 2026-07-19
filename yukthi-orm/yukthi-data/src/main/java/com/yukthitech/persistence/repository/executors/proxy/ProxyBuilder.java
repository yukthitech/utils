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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.yukthitech.utils.exceptions.InvalidStateException;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class ProxyBuilder
{
	/**
	 * Synthetic field on generated proxy classes that holds the per-instance handler.
	 */
	private static final String HANDLER_FIELD = "$__handler";
	
	/**
	 * Cache of generated proxy types keyed by base + optional interface type.
	 */
	private static final ConcurrentHashMap<ProxyKey, ProxyType> PROXY_CACHE = new ConcurrentHashMap<>();
	
	private static final class ProxyKey
	{
		private final Class<?> baseType;
		private final Class<?> interfaceType;
		
		private ProxyKey(Class<?> baseType, Class<?> interfaceType)
		{
			this.baseType = baseType;
			this.interfaceType = interfaceType;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
			{
				return true;
			}
			
			if(!(obj instanceof ProxyKey))
			{
				return false;
			}
			
			ProxyKey other = (ProxyKey) obj;
			return Objects.equals(baseType, other.baseType)
					&& Objects.equals(interfaceType, other.interfaceType);
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(baseType, interfaceType);
		}
	}
	
	private static final class ProxyType
	{
		private final Class<?> proxyClass;
		private final Field handlerField;
		
		private ProxyType(Class<?> proxyClass)
		{
			try
			{
				this.proxyClass = proxyClass;
				this.handlerField = proxyClass.getDeclaredField(HANDLER_FIELD);
				this.handlerField.setAccessible(true);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Failed to prepare proxy type: {}", proxyClass.getName(), ex);
			}
		}
	}
	
	public static Object buildProxy(Class<?> baseType, Class<?> interfaceType, InvocationHandler handler)
	{
		ProxyType proxyType = PROXY_CACHE.computeIfAbsent(
				new ProxyKey(baseType, interfaceType),
				key -> new ProxyType(createProxyClass(baseType, interfaceType)));
		
		try
		{
			Object proxy = proxyType.proxyClass.getConstructor().newInstance();
			proxyType.handlerField.set(proxy, handler);
			return proxy;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating instance of dynamic type", ex);
		}
	}
	
	private static Class<?> createProxyClass(Class<?> baseType, Class<?> interfaceType)
	{
		DynamicType.Builder<?> builder = new ByteBuddy()
			.subclass(baseType);
		
		ClassLoader classLoader = baseType.getClassLoader();
		
		if(interfaceType != null)
		{
			builder = builder.implement(interfaceType);
			classLoader = interfaceType.getClassLoader();
		}
		
		return builder
			.defineField(HANDLER_FIELD, InvocationHandler.class, Visibility.PRIVATE)
			.method(ElementMatchers.any()).intercept(InvocationHandlerAdapter.toField(HANDLER_FIELD))
			.make()
			.load(classLoader)
			.getLoaded();
	}
}
