package com.yukthitech.utils.expr;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simple implementation of the function.
 */
public class SimpleJavaFunction implements IFunction
{
	/**
	 * Name of the function.
	 */
	private String name;
	
	/**
	 * Syntax of the function.
	 */
	private String syntax;
	
	/**
	 * Description of the function.
	 */
	private String description;
	
	/**
	 * Parameter list of the function.
	 */
	private int paramList[];
	
	/**
	 * Java method to be invoked.
	 */
	private Method method;
	
	/**
	 * Instantiates a new simple java function.
	 *
	 * @param info the info
	 * @param method the method
	 */
	public SimpleJavaFunction(FunctionInfo info, Method method)
	{
		name = info.name();
		syntax = info.syntax();
		description = info.description();
		paramList = info.matchParameterTypes().length > 0 ? info.matchParameterTypes() : null;
		
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IFunction#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IFunction#getSyntax()
	 */
	@Override
	public String getSyntax()
	{
		return syntax;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IFunction#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IFunction#getReturnType(java.lang.Class[])
	 */
	@Override
	public Class<?> getReturnType(Class<?>[] paramTypes)
	{
		//do basic validation on parameter types
		Class<?> funcParamTypes[] = this.method.getParameterTypes();
		int lastIdx = funcParamTypes.length - 1;
		int lastValidateIdx = 0;
		
		for(int i = 0; i < funcParamTypes.length; i++)
		{
			//if last parameter type is array (var args)
			if(i == lastIdx && funcParamTypes[i].isArray())
			{
				//ensure parameter from this point is matching with var args type
				for(int j = i; j < paramTypes.length; j++)
				{
					if(!funcParamTypes[i].getComponentType().isAssignableFrom(paramTypes[j]))
					{
						throw new InvalidArgumentException("For function {} invalid parameter type specified at index {}. Expected: {}, Found: {}", 
								name, j, funcParamTypes[i].getComponentType().getSimpleName(), paramTypes[j].getSimpleName());
					}
					
					lastValidateIdx++;
				}
				
				break;
			}
			
			if(i >= paramTypes.length)
			{
				throw new InvalidArgumentException("For function {}  insufficient number of parameters specified", name);
			}
			
			//for normal types perform direct check
			if(!funcParamTypes[i].isAssignableFrom(paramTypes[i]))
			{
				throw new InvalidArgumentException("For function {} invalid parameter type specified at index {}. Expected: {}, Found: {}", 
						name, i, funcParamTypes[i].getSimpleName(), paramTypes[i].getSimpleName());
			}
			
			lastValidateIdx++;
		}
		
		if(funcParamTypes.length > 0 && lastValidateIdx < paramTypes.length)
		{
			throw new InvalidArgumentException("For function {} invlaid number parameter types specified. Expected count: {}, Found: {}", 
					name, funcParamTypes.length, paramTypes.length);
		}
		
		//if return type is not based on parameter types
		if(this.paramList == null)
		{
			return method.getReturnType();
		}
		
		//if return type is based on parameter types
		Class<?> firstParamType = paramTypes[this.paramList[0]];
		
		if(paramList.length == 1)
		{
			return firstParamType;
		}
		
		for(int i = 1; i < paramList.length; i++)
		{
			if(!paramTypes[paramList[i]].isAssignableFrom(firstParamType))
			{
				firstParamType = firstParamType.getSuperclass();
				i = 0;
				continue;
			}
		}

		return firstParamType;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.utils.expr.IFunction#evaluate(java.lang.Object[])
	 */
	@Override
	public Object evaluate(Object[] parameters)
	{
		List<Object> methodArgs = new ArrayList<Object>();
		
		Class<?> funcParamTypes[] = this.method.getParameterTypes();
		int lastIdx = this.method.getParameterTypes().length - 1;
		
		for(int i = 0; i < funcParamTypes.length; i++)
		{
			//if last parameter type is array (var args)
			if(i == lastIdx && funcParamTypes[i].isArray())
			{
				int leftCount = parameters.length - i;
				Object array[] = (Object[]) Array.newInstance(funcParamTypes[i].getComponentType(), leftCount);
				
				for(int j = i, k = 0; j < parameters.length; j++, k++)
				{
					array[k] = parameters[j];
				}
				
				methodArgs.add(array);
				break;
			}
			
			//when parameter type is not matching, try conversion
			if(!funcParamTypes[i].isAssignableFrom(parameters[i].getClass()))
			{
				parameters[i] = ConvertUtils.convert(parameters[i], funcParamTypes[i]);
			}
			
			methodArgs.add(parameters[i]);
		}

		try
		{
			return method.invoke(null, (Object[]) methodArgs.toArray());
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while invoking function - " + name);
		}
	}
}
