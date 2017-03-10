package com.yukthitech.automation.test.jobj.steps;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.DbPlugin;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.utils.CommonUtils;

/**
 * Step to invoke method on target object.
 */
@Executable(name = "invokeMethod", requiredPluginTypes = DbPlugin.class, message = "Executes specified method on specified bean.")
public class InvokeMethodStep extends AbstractStep
{
	/**
	 * Object on which method needs to be invoked.
	 */
	@Param(description = "Object on which method needs to be invoked. For non-static method this is mandatory", required = false)
	private Object object;
	
	/**
	 * Object on which method needs to be invoked. For static method this is mandatory.
	 */
	@Param(description = "Object on which method needs to be invoked. For static method this is mandatory", required = false)
	private Class<?> objectType;

	/**
	 * Name of the method to be invoked.
	 */
	@Param(description = "Name of the method to be invoked.") 
	private String name;
	
	/**
	 * List of method argument types delimited by comma. Needs to be used when particular method needs to be invoked.
	 * If not specified, method which matches with specified arguments will be invoked.
	 */
	@Param(description = "List of method argument types delimited by comma. Needs to be used when particular method needs to be invoked."
		+ "\nIf not specified, method which matches with specified arguments will be invoked.", required = false)
	private String paramTypes;
	
	/**
	 * Flag indicating if the method to be invoked is a static method or normal instance method.
	 */
	@Param(description = "Flag indicating if the method to be invoked is a static method or normal instance method. \nDefaults: false", required = false)
	private boolean isStatic = false;
	
	/**
	 * List of parameters to be passed to method.
	 */
	@Param(description = "List of parameters to be passed to method.", required = false)
	private List<Object> parameters = new ArrayList<>();
	
	/**
	 * Context parameter name to be used to set the result on context.
	 */
	@Param(description = "Context parameter name to be used to set the result on context. \nDefault: $result", required = false)
	private String resultParameter = "returnValue";
	
	/**
	 * Sets the object on which method needs to be invoked.
	 *
	 * @param object the new object on which method needs to be invoked
	 */
	public void setObject(Object object)
	{
		this.object = object;
	}

	/**
	 * Sets the name of the method to be invoked.
	 *
	 * @param name the new name of the method to be invoked
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the list of method argument types delimited by comma. Needs to be used when particular method needs to be invoked. If not specified, method which matches with specified arguments will be invoked.
	 *
	 * @param paramTypes the new list of method argument types delimited by comma
	 */
	public void setParamTypes(String paramTypes)
	{
		if(StringUtils.isBlank(paramTypes))
		{
			paramTypes = null;
		}
		
		this.paramTypes = paramTypes;
	}

	/**
	 * Sets the list of arguments to be passed to method.
	 *
	 * @param parameters the new list of arguments to be passed to method
	 */
	public void addParameter(Object param)
	{
		this.parameters.add(param);
	}
	
	/**
	 * Flag indicating if the method to be invoked is a static method or normal instance method.
	 * @param isStatic indicates if it is static method.
	 */
	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}
	
	/**
	 * Sets the object on which method needs to be invoked. For static method this is mandatory.
	 *
	 * @param objectType the new object on which method needs to be invoked
	 */
	public void setObjectType(Class<?> objectType)
	{
		this.objectType = objectType;
	}
	
	/**
	 * Sets the context parameter name to be used to set the result on context.
	 *
	 * @param resultParameter the new context parameter name to be used to set the result on context
	 */
	public void setResultParameter(String resultParameter)
	{
		this.resultParameter = resultParameter;
	}

	/**
	 * Extracts the class types from types string.
	 * @param typesString string to be parsed
	 * @return class types
	 */
	private static Class<?>[] getParameterTypes(String typesString)
	{
		if(StringUtils.isBlank(typesString))
		{
			return null;
		}
		
		String typeNames[] = typesString.trim().split("\\s*\\,\\s*");
		
		Class<?> types[] = new Class<?>[typeNames.length];
		int idx = 0;
		
		for(String type : typeNames)
		{
			types[idx] = CommonUtils.getClass(type);
			idx++;
		}
		
		return types;
	}
	
	private Object invokeMethod(ExecutionLogger logger) throws Exception
	{
		Object params[] = parameters.isEmpty() ? null : parameters.toArray(new Object[0]);
		Class<?> types[] = getParameterTypes(this.paramTypes);
		
		try
		{
			if(isStatic)
			{
				if(paramTypes != null)
				{
					logger.debug("On class {} invoking static method '{}' with parameter types - {}", objectType.getName(), name, paramTypes);
					logger.debug("Params used: {}", parameters);
					
					return MethodUtils.invokeExactStaticMethod(objectType, name, params, types);
				}
				else
				{
					logger.debug("On class {} invoking static method '{}' matching with specified parameters", objectType.getName(), name);
					logger.debug("Params used: {}", parameters);
					
					return MethodUtils.invokeStaticMethod(objectType, name, params);
				}
			}
			
			if(paramTypes != null)
			{
				logger.debug("On object {} invoking instance method '{}' with parameter types - {}", object, name, paramTypes);
				logger.debug("Params used: {}", parameters);

				return MethodUtils.invokeExactMethod(object, name, params, types);
			}
			else
			{
				logger.debug("On object {} invoking instance method '{}' matching with specified parameters", object, name);
				logger.debug("Params used: {}", parameters);
				
				return MethodUtils.invokeMethod(object, name, params);
			}
		} catch(Exception ex)
		{
			//if the exception is invocation target throw root cause
			if(ex instanceof InvocationTargetException)
			{
				ex = (Exception) ex.getCause();
			}

			logger.error(ex, "An error occurred while invoking method - {}", name);
			
			throw ex;
		} 
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.IStep#execute(com.yukthitech.automation.AutomationContext, com.yukthitech.automation.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger logger) throws Exception
	{
		Object result = invokeMethod(logger);
		context.setAttribute(resultParameter, result);
	}

	@Override
	public void validate() throws ValidateException
	{
		if(isStatic && objectType == null)
		{
			throw new ValidateException("No object-type specified for static method invocation.");
		}
		
		if(!isStatic && object == null)
		{
			throw new ValidateException("No/invalid object specified for instance method invocation.");
		}
	}
}
