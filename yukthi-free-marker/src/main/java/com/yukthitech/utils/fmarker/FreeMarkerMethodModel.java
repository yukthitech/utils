package com.yukthitech.utils.fmarker;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * Model wrapper for free marker dynamic method registration.
 * @author akiran
 */
class FreeMarkerMethodModel implements TemplateMethodModelEx
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
	public FreeMarkerMethodModel(Method freeMarkerMethod, String methodName)
	{
		this.freeMarkerMethod = freeMarkerMethod;
		this.methodName = methodName;
	}
	
	/**
	 * Converts the specified argument into required type.
	 * @param argument Argument value to be converted
	 * @param requiredType Expected type
	 * @return converted object value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object convertArgument(Object argument, Class<?> requiredType) throws TemplateModelException
	{
		if(argument == null)
		{
			return null;
		}
		
		if(argument instanceof TemplateModel)
		{
			argument = DeepUnwrap.unwrap((TemplateModel)argument);
		}
		
		if(requiredType.isAssignableFrom(argument.getClass()))
		{
			return argument;
		}
		
		if(argument instanceof Collection)
		{
			if(List.class.isAssignableFrom(requiredType))
			{
				return new ArrayList( (Collection) argument );
			}
			else if(Set.class.isAssignableFrom(requiredType))
			{
				return new HashSet( (Collection) argument );
			}
			else if(Collection.class.isAssignableFrom(requiredType))
			{
				return (Collection) argument ;
			}
		}
		
		return ConvertUtils.convert(argument, requiredType);
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
		Object methodArgs[] = null;
		int argsSize = arguments != null ? arguments.size() : 0;
		
		if(!isVarArgs)
		{
			if(argsSize != argTypes.length)
			{
				throw new InvalidArgumentException("Invalid number of arguments specified for method - {} [Expected count: {}, Actual Count: {}, Arguments: {}]", 
						methodName, argTypes.length, argsSize, arguments);
			}

			//for normal arguments, number of method arguments will be equal to actual arguments
			methodArgs = new Object[arguments != null ? arguments.size() : 0];
		}
		else
		{
			if(argsSize < argTypes.length - 1)
			{
				throw new InvalidArgumentException("Invalid number of arguments specified for method - {} [Expected min count: {}, Actual Count: {}, Arguments: {}]", 
						methodName, argTypes.length - 1, argsSize, arguments);
			}
			
			//for var args number of arguments will be equal to number of declared parameters in method
			// 	last + extra param will be clubbed into single array for varargs
			methodArgs = new Object[argTypes.length];
		}
		
		if(argsSize > 0)
		{
			int stdArgCount = isVarArgs ? argTypes.length - 1 : argTypes.length;
			
			for(int i = 0; i < stdArgCount; i++)
			{
				methodArgs[i] = convertArgument(arguments.get(i), argTypes[i]);
			}
			
			if(isVarArgs && argsSize >= argTypes.length)
			{
				Class<?> varArgType = argTypes[argTypes.length - 1].getComponentType();
				Object varArgs = Array.newInstance(varArgType, argsSize - stdArgCount); 
				
				for(int i = stdArgCount, j = 0; i < argsSize; i++, j++)
				{
					Array.set( varArgs, j, convertArgument(arguments.get(i), varArgType) );
				}
				
				methodArgs[stdArgCount] = varArgs;
			}
		}

		try
		{
			return freeMarkerMethod.invoke(null, methodArgs);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking method '{}'. "
					+ "\nJava Method: {}"
					+ "\nArguments used: {}"
					+ "\nArguments type: {}",
					freeMarkerMethod.getName(), freeMarkerMethod, Arrays.toString( methodArgs ), Arrays.toString( getTypes(methodArgs) ), ex);
		}
	}
}
