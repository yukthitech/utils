package com.yukthitech.autox.context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.config.IPluginSession;
import com.yukthitech.autox.config.PluginManager;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * One instance of this class will be created per thread. Threads which are created
 * as part of single execution will be sharing {@link #executionContextStack}.
 * 
 * @author akranthikiran
 */
public class ExecutionThreadStack
{
	private ExecutionContext executionContext;
	
	private Map<Class<?>, IPluginSession> pluginSessions = new HashMap<>();
	
	/**
	 * Used to maintain function parameter stack.
	 */
	private Stack<Map<String, Object>> parametersStack = new Stack<>();

	/**
	 * Manages the stack trace of execution.
	 */
	ExecutionStack executionStack = new ExecutionStack();
	
	public ExecutionThreadStack(ExecutionContext context)
	{
		this.executionContext = context;
	}
	
	public void setExecutionContext(ExecutionContext executionContext)
	{
		this.executionContext = executionContext;
	}

	public ExecutionContext getExecutionContext()
	{
		return executionContext;
	}
	
	public void clearExecutionContext()
	{
		this.executionContext = null;
		
		pluginSessions.values().forEach(session -> session.release());
		pluginSessions.clear();
	}

	@SuppressWarnings("unchecked")
	public <P, S extends IPluginSession> S getPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		IPluginSession session = pluginSessions.get(pluginType);
		
		if(session == null)
		{
			IPlugin<?, ?> plugin = PluginManager.getInstance().getPlugin(pluginType);
			session = plugin.newSession();
			
			pluginSessions.put(pluginType, session);
		}
		
		return (S) session;
	}
	
	public Collection<IPluginSession> getPluginSessions()
	{
		return pluginSessions.values();
	}

	public void pushParameters(Map<String, Object> parameters)
	{
		if(parameters == null)
		{
			parameters = Collections.emptyMap();
		}
		
		parametersStack.push(parameters);
	}

	public void popParameters()
	{
		parametersStack.pop();
	}
	
	public boolean isParamPresent()
	{
		return !parametersStack.isEmpty();
	}
	
	public Map<String, Object> getParam()
	{
		if(parametersStack.isEmpty())
		{
			throw new InvalidStateException("Parameters are accessed outside the function");
		}
		
		return (Map<String, Object>) parametersStack.peek();
	}
	
	public Object getParameter(String name)
	{
		Map<String, Object> paramMap = (Map<String, Object>) getParam();
		
		if(paramMap == null)
		{
			return null;
		}
		
		return paramMap.get(name);
	}
}
