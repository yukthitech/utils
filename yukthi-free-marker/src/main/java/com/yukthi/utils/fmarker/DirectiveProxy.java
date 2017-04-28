package com.yukthi.utils.fmarker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Proxy for invoking directive methods.
 * @author akiran
 */
class DirectiveProxy implements TemplateDirectiveModel
{
	/**
	 * Target method to be invoked.
	 */
	private Method method;
	
	/**
	 * Instantiates a new directive proxy.
	 *
	 * @param method the method
	 */
	public DirectiveProxy(Method method)
	{
		this.method = method;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
	{
		try
		{
			method.invoke(null, env, params, loopVars, body);
		} catch(InvocationTargetException e)
		{
			Throwable ex = e.getCause();
			
			if(ex instanceof TemplateException)
			{
				throw (TemplateException) ex;
			}
			
			if(ex instanceof IOException)
			{
				throw (IOException) ex;
			}
			
			throw new InvalidStateException("An error occurred while executing directiv method: {}.{}", method.getDeclaringClass().getName(), method.getName(), e);
		} catch(Exception e)
		{
			throw new InvalidStateException("An error occurred while executing directiv method: {}.{}", method.getDeclaringClass().getName(), method.getName(), e);
		}
	}

}
