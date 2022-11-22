package com.yukthitech.autox.context;

import java.util.IdentityHashMap;
import java.util.Map;

import com.yukthitech.autox.config.IPlugin;
import com.yukthitech.autox.config.IPluginSession;
import com.yukthitech.autox.exec.Executor;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages execution scopes across different executions/threads.
 * @author akranthikiran
 */
public class ExecutionContextManager
{
	private static ExecutionContextManager instance = new ExecutionContextManager();
	
	private ThreadLocal<ExecutionThreadStack> executionThreadLocal = new ThreadLocal<>();
	
	Map<Executor, ExecutionContext> executorContexts = new IdentityHashMap<>();
	
	/**
	 * Context which is created first (which would be test-suite-group based in general).
	 */
	private ExecutionContext globalContext;
	
	private ExecutionContextManager()
	{}
	
	public static ExecutionContextManager getInstance()
	{
		return instance;
	}
	
	public void push(Executor executor)
	{
		ExecutionThreadStack executionContextStack = executionThreadLocal.get();
		
		if(executionContextStack == null)
		{
			executionContextStack = new ExecutionThreadStack();
			executionThreadLocal.set(executionContextStack);
		}
		
		ExecutionContext executionContext = new ExecutionContext(this, executor);
		executionContextStack.pushExecutionContext(executionContext);
		
		if(globalContext == null)
		{
			globalContext = executionContext;
		}
		
		executionContextStack.executionStack.push(executor.getExecutable());
	}

	public void pop(Executor executor)
	{
		ExecutionThreadStack executionContextStack = executionThreadLocal.get();
		
		if(executionContextStack == null || executionContextStack.isExecutionContextEmpty() || executionContextStack.peekExecutionContext().executor != executor)
		{
			throw new InvalidStateException("Executor being poped is not same executor found on stack. [Executor on stack: {}, Executor being poped: {}]", 
					executionContextStack.peekExecutionContext().executor, executor);
		}
		
		executionContextStack.executionStack.pop(executor.getExecutable());
		executionContextStack.popExecutionContext();
		
		if(executionContextStack.isExecutionContextEmpty())
		{
			executionContextStack.close();
			executionThreadLocal.remove();
		}
	}
	
	public static ExecutionContext getExecutionContext()
	{
		return instance.getCurrentContext();
	}
	
	public ExecutionThreadStack getExecutionContextStack()
	{
		ExecutionThreadStack executionContextStack = executionThreadLocal.get();
		
		if(executionContextStack == null || executionContextStack.isExecutionContextEmpty())
		{
			throw new NonExecutionThreadException("Execution context method is invoked by non-executor thread");
		}

		return executionContextStack;
	}
	
	private ExecutionContext getCurrentContext()
	{
		return getExecutionContextStack().peekExecutionContext();
	}
	
	public <P, S extends IPluginSession> S getPluginSession(Class<? extends IPlugin<?, S>> pluginType)
	{
		return getExecutionContextStack().getPluginSession(pluginType);
	}
	
	public ExecutionStack getExecutionStack()
	{
		return getExecutionContextStack().executionStack;
	}
	
	public void setGlobalAttribute(String name, Object value)
	{
		if(globalContext == null)
		{
			throw new InvalidStateException("No global context is created yet");
		}
		
		globalContext.setAttribute(name, value);
	}
	
	public Object getGlobalAttribute(String name)
	{
		if(globalContext == null)
		{
			throw new InvalidStateException("No global context is created yet");
		}
		
		return globalContext.getAttribute(name);
	}
}
