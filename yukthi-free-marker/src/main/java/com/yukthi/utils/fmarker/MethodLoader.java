package com.yukthi.utils.fmarker;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.fmarker.annotaion.FreeMarkerDirective;
import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.TemplateDirectiveModel;

/**
 * Used by free marker to load methods of target class.
 * @author akiran
 */
class MethodLoader
{
	private static Logger logger = LogManager.getLogger(MethodLoader.class);
	
	/**
	 * Expected attributes of directive method types.
	 */
	private static Class<?> DIR_ATTR_TYPES[];
	
	/**
	 * Expected attributes of directive method types string.
	 */
	private static StringBuilder DIR_ATTR_TYPES_STR = new StringBuilder();
	
	static
	{
		Method methods[] = TemplateDirectiveModel.class.getMethods();
		
		for(Method met : methods)
		{
			if("execute".equals(met.getName()))
			{
				DIR_ATTR_TYPES = met.getParameterTypes();
				DIR_ATTR_TYPES_STR.append("[");
				
				for(Class<?> type: DIR_ATTR_TYPES)
				{
					if(DIR_ATTR_TYPES_STR.length() > 1)
					{
						DIR_ATTR_TYPES_STR.append(", ");
					}
					
					DIR_ATTR_TYPES_STR.append(type.getName());
				}
				
				DIR_ATTR_TYPES_STR.append("]");
				
				break;
			}
		}
	}
	
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
				logger.debug("Non-static method {}.{}() is marked as free-marker method/directive", cls.getName(), method.getName());
				continue;
			}
			
			if(freeMarkerDirective != null)
			{
				if(!Arrays.equals(DIR_ATTR_TYPES, method.getParameterTypes()))
				{
					throw new InvalidStateException("For directive method {}.{} argument types are not as not expected. Expected argument types: {}", cls.getName(), method.getName(), DIR_ATTR_TYPES_STR);
				}
				
				name = freeMarkerDirective.value();
				name = StringUtils.isBlank(name) ? method.getName() : name;
				
				freeMarkerEngine.registerDirective(name, new DirectiveProxy(method));
				continue;
			}
			
			name = freeMarkerMethod.value();
			name = StringUtils.isBlank(name) ? method.getName() : name;
			freeMarkerEngine.registerMethod(name, method);
		}
	}
}
