package com.yukthitech.autox.ide.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Collection of actions.
 * @author akiran
 */
@Component
public class ActionCollection
{
	private static Logger logger = LogManager.getLogger(ActionCollection.class);
	
	/**
	 * Executable actions.
	 * @author akiran
	 */
	public static class ExecutableAction
	{
		/**
		 * Object which holds the action method.
		 */
		private Object object;
		
		/**
		 * Method to be invoked.
		 */
		private Method method;

		public ExecutableAction(Object object, Method method)
		{
			this.object = object;
			this.method = method;
		}
	}
	
	private Map<String, ExecutableAction> actions = new HashMap<>();
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@PostConstruct
	private void init()
	{
		Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ActionHolder.class);
		
		for(Object bean : beanMap.values())
		{
			addActionHolder(bean, AopProxyUtils.ultimateTargetClass(bean));
		}
	}
	
	private void addActionHolder(Object actionHolder, Class<?> type)
	{
		logger.debug("Registering action holder of type: {}", type.getName());
		
		Method methods[] = type.getMethods();
		
		for(Method met : methods)
		{
			if(Modifier.isStatic(met.getModifiers()) || met.getAnnotation(Action.class) == null || met.getParameterTypes().length > 0)
			{
				continue;
			}
			
			actions.put(met.getName(), new ExecutableAction(actionHolder, met));
		}
	}
	
	public void invokeAction(String name)
	{
		logger.debug("Invoking action with name: {}", name);
		
		try
		{
			ExecutableAction action = actions.get(name);
			action.method.invoke(action.object);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while invoking action: {}", name, ex);
		}
	}
	
	public ActionListener getActionListener(String action)
	{
		if(!actions.containsKey(action))
		{
			throw new InvalidArgumentException("Invalid action name specified: " + action);
		}
		
		ActionListener listener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				invokeAction(action);
			}
		};
		
		return listener;
	}
}
