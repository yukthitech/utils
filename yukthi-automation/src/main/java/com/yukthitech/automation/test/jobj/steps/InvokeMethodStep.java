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
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.common.AutomationUtils;
import com.yukthitech.automation.config.DbPlugin;
import com.yukthitech.automation.test.ObjectCopy;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.utils.CommonUtils;

/**
 * Step to invoke method on target object.
 */
@Executable(name = "invokeMethod", requiredPluginTypes = DbPlugin.class, message = "Executes specified method on specified bean.")
public class InvokeMethodStep extends AbstractStep
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
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
	private String method;
	
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
	@Param(description = "Context parameter name to be used to set the result on context. \nDefault: returnValue", required = false)
	private String resultParameter = "returnValue";
	
	/**
	 * When set to false, object will not be deep cloned. Which means property expressions if any, will be processed only once. Default false.
	 */
	@Param(description = "When set to false, object will not be deep cloned. Which means property expressions if any, will be processed only once.\nDefault false", required = false)
	private boolean deepCloneObject = false;
	
	/**
	 * When set to false, parameters will not be deep cloned. Which means property expressions if any, will be processed only once. Default false.
	 */
	@Param(description = "When set to false, parameters will not be deep cloned. Which means property expressions if any, will be processed only once.\nDefault false", required = false)
	private boolean deepCloneParams = false;
	
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
	public void setMethod(String name)
	{
		this.method = name;
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
	 * Sets the when set to false, object will not be deep cloned. Which means property expressions if any, will be processed only once. Default false.
	 *
	 * @param deepCloneObject the new when set to false, object will not be deep cloned
	 */
	public void setDeepCloneObject(boolean deepCloneObject)
	{
		this.deepCloneObject = deepCloneObject;
	}

	/**
	 * Sets the when set to false, parameters will not be deep cloned. Which means property expressions if any, will be processed only once. Default false.
	 *
	 * @param deepCloneParams the new when set to false, parameters will not be deep cloned
	 */
	public void setDeepCloneParams(boolean deepCloneParams)
	{
		this.deepCloneParams = deepCloneParams;
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
	
	/**
	 * Invoke method.
	 *
	 * @param logger the logger
	 * @return the object
	 * @throws Exception the exception
	 */
	private Object invokeMethod(ExecutionLogger logger) throws Exception
	{
		Object params[] = parameters.isEmpty() ? null : parameters.toArray(new Object[0]);
		Class<?> types[] = getParameterTypes(this.paramTypes);
		
		Object object = this.object;
		
		//if object is instanceof object-copy, create copy instance
		if(object instanceof ObjectCopy)
		{
			object = ((ObjectCopy) object).createCopy();
		}
		
		//check if any of params is copy-object create copy of current param
		if(params != null)
		{
			for(int i = 0; i < params.length; i++)
			{
				if(params[i] instanceof ObjectCopy)
				{
					params[i] = ((ObjectCopy) params[i]).createCopy();
				}
			}
		}
		
		try
		{
			if(isStatic)
			{
				if(paramTypes != null)
				{
					logger.debug("On class {} invoking static method '{}' with parameter types - {}", objectType.getName(), method, paramTypes);
					logger.debug("Params used: {}", parameters);
					
					return MethodUtils.invokeExactStaticMethod(objectType, method, params, types);
				}
				else
				{
					logger.debug("On class {} invoking static method '{}' matching with specified parameters", objectType.getName(), method);
					logger.debug("Params used: {}", parameters);
					
					return MethodUtils.invokeStaticMethod(objectType, method, params);
				}
			}
			
			if(paramTypes != null)
			{
				logger.debug("On object {} invoking instance method '{}' with parameter types - {}", object, method, paramTypes);
				logger.debug("Params used: {}", parameters);

				return MethodUtils.invokeExactMethod(object, method, params, types);
			}
			else
			{
				logger.debug("On object {} invoking instance method '{}' matching with specified parameters", object, method);
				logger.debug("Params used: {}", parameters);
				
				return MethodUtils.invokeMethod(object, method, params);
			}
		} catch(Exception ex)
		{
			//if the exception is invocation target throw root cause
			if(ex instanceof InvocationTargetException)
			{
				ex = (Exception) ex.getCause();
			}

			logger.error(ex, "An error occurred while invoking method - {}", method);
			
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

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.AbstractStep#validate()
	 */
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

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.AbstractStep#clone()
	 */
	@Override
	public IStep clone()
	{
		InvokeMethodStep clone = (InvokeMethodStep) super.clone();
		
		if(deepCloneObject)
		{
			clone.object = AutomationUtils.deepClone(object);
		}
		
		if(deepCloneParams)
		{
			clone.parameters = AutomationUtils.deepClone(this.parameters);
		}
		
		return clone;
	}
}