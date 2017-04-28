package com.yukthi.utils.fmarker;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

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

	/* (non-Javadoc)
	 * @see freemarker.template.TemplateMethodModelEx#exec(java.util.List)
	 */
	@SuppressWarnings("rawtypes")
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
				throw new InvalidArgumentException("Invalid number of arguments specified for method - {}", methodName);
			}

			//for normal arguments, number of method arguments will be equal to actual arguments
			methodArgs = new Object[arguments != null ? arguments.size() : 0];
		}
		else
		{
			if(argsSize < argTypes.length - 1)
			{
				throw new InvalidArgumentException("Invalid number of arguments specified for method - {}", methodName);
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
				methodArgs[i] = ConvertUtils.convert(arguments.get(i), argTypes[i]);
			}
			
			if(isVarArgs && argsSize >= argTypes.length)
			{
				Class<?> varArgType = argTypes[argTypes.length - 1].getComponentType();
				Object varArgs = Array.newInstance(varArgType, argsSize - stdArgCount); 
				
				for(int i = stdArgCount, j = 0; i < argsSize; i++, j++)
				{
					Array.set( varArgs, j, ConvertUtils.convert(arguments.get(i), varArgType) );
				}
				
				methodArgs[stdArgCount] = varArgs;
			}
		}

		try
		{
			return freeMarkerMethod.invoke(null, methodArgs);
		}catch(Exception ex)
		{
			throw new TemplateModelException(ex);
		}
	}
}
