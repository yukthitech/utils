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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.fmarker.annotaion.FreeMarkerDirective;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

/**
 * Used by free marker to load methods of target class.
 * @author akiran
 */
class MethodLoader
{
	private static Logger logger = Logger.getLogger(MethodLoader.class.getName());
	
	/**
	 * Loads the free marker methods from specified class
	 * @param cls class to load
	 * @param freeMarkerEngine Free marker engine to which methods should be loaded
	 */
	public static void loadClass(Class<?> cls, FreeMarkerEngine freeMarkerEngine)
	{
		Method methods[] = cls.getMethods();
		
		FreeMarkerMethod freeMarkerMethod = null;
		FreeMarkerDirective freeMarkerDirective = null;
		String name = null;
		
		for(Method method : methods)
		{
			freeMarkerMethod = method.getAnnotation(FreeMarkerMethod.class);
			freeMarkerDirective = method.getAnnotation(FreeMarkerDirective.class);
			
			if(freeMarkerMethod == null && freeMarkerDirective == null)
			{
				continue;
			}
			
			//ignore non-static methods
			if(!Modifier.isStatic(method.getModifiers()))
			{
				logger.log(Level.FINE, String.format("Non-static method %s.%s() is marked as free-marker method/directive", cls.getName(), method.getName()));
				continue;
			}
			
			if(freeMarkerDirective != null)
			{
				name = freeMarkerDirective.value();
				name = StringUtils.isBlank(name) ? method.getName() : name;
				
				freeMarkerEngine.registerDirective(name, method);
				continue;
			}
			
			name = freeMarkerMethod.value();
			name = StringUtils.isBlank(name) ? method.getName() : name;
			freeMarkerEngine.registerMethod(name, method);
		}
	}
}
