package com.yukthitech.autox.exec;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.config.ErrorDetails;
import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.StepExecutor;

public class ExecutorUtils
{
	private static Logger logger = LogManager.getLogger(ExecutorUtils.class);
	
	public static void invokeErrorHandling(Executable executable, ErrorDetails errorDetails)
	{
		logger.debug( "Invoking plugin error handling for executable: {}", executable.name() );
		
		AutomationContext context = AutomationContext.getInstance();
		Collection< IPlugin<?> > pluginTypes = context.getPlugins();
		
		if(pluginTypes == null || pluginTypes.isEmpty())
		{
			logger.debug( "No associated plugins found in current context.");
			return;
		}
		
		for(IPlugin<?>  plugin : pluginTypes)
		{
			if(plugin == null)
			{
				continue;
			}
			
			logger.debug("Invoking error handling of plugin - {}", plugin.getClass().getName());
			
			try
			{
				plugin.handleError(context, errorDetails);
			}catch(Exception ex)
			{
				logger.error("An error occurred during plugin-error-handling with plugin: {}", plugin, ex);
			}
		}
	}
	
	/**
	 * Creates executable proxy annotation for specified step-group.
	 * @param function
	 * @return
	 */
	public static Executable createExecutable(final Function function)
	{
		Executable executable = (Executable) Proxy.newProxyInstance(StepExecutor.class.getClassLoader(), new Class[] {Executable.class}, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				String methodName = method.getName();
				
				if("name".equals(methodName) || "message".equals(methodName))
				{
					return "Function-" + function.getName();
				}
				
				return null;
			}
		});

		return executable;
	}


}
