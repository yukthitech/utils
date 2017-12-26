package com.yukthitech.utils.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manager for managing listeners.
 * @author akiran
 * @param <L> listener type.
 */
public class EventListenerManager<L>
{
	private static Logger logger = LogManager.getLogger(EventListenerManager.class);
	
	/**
	 * Event listener proxy object.
	 */
	private L proxy;
	
	/**
	 * Listeners list.
	 */
	private List<L> listeners = new ArrayList<L>();
	
	/**
	 * Thread pool for executing listeners.
	 */
	private ScheduledExecutorService threadPool;
	
	private boolean parallel;
	
	private EventListenerManager(boolean parallel)
	{
		if(parallel)
		{
			threadPool = Executors.newScheduledThreadPool(10);
		}
	}

	/**
	 * Sets the proxy object for invocation.
	 * @param proxy
	 */
	private void setProxy(L proxy)
	{
		this.proxy = proxy;
	}
	
	/**
	 * Gets the object of listener on which event method can be invoked
	 * which would get delegated to actual listeners.
	 * @return underlying event listener type object.
	 */
	public L get()
	{
		return proxy;
	}
	
	/**
	 * Adds the specified listener.
	 * @param listener listener to add.
	 */
	public void addListener(L listener)
	{
		if(listener == null)
		{
			throw new NullPointerException("Listener can not be null.");
		}
		
		listeners.add(listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener
	 * @return true if listener is found and removed.
	 */
	public boolean removeListener(L listener)
	{
		return listeners.remove(listener);
	}
	
	/**
	 * Fetches the listeners added to this manager.
	 * @return
	 */
	public Collection<L> getListeners()
	{
		return new ArrayList<L>(listeners);
	}
	
	/**
	 * Invokes specified method on specified listener with specified params.
	 * @param listener listener to be invoked
	 * @param method method to be invoked
	 * @param params params to be passed
	 */
	private void invokeListener(L listener, Method method, Object... params)
	{
		try
		{
			method.invoke(listener, params);
		}catch(Exception ex)
		{
			logger.error("An error occurred while while invoking listener - {}", listener, ex);
		}
	}
	
	/**
	 * Invokes the specified method on all listeners. This is synchronized method 
	 * which ensures events are delivered in same sequence.
	 * But note: listener invocation sequence is not guaranteed.
	 * @param method
	 * @param params
	 */
	private synchronized void invokeMethod(final Method method, final Object... params)
	{
		for(L listener : listeners)
		{
			if(!parallel)
			{
				invokeListener(listener, method, params);
				continue;
			}
			
			final L finalListener = listener;
			
			threadPool.execute(new Runnable()
			{
				@Override
				public void run()
				{
					invokeListener(finalListener, method, params);
				}
			});
		}
	}
	
	/**
	 * Factory method to create event listener manager.
	 * @param listenerType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <L> EventListenerManager<L> newEventListenerManager(Class<L> listenerType, boolean parallel)
	{
		final EventListenerManager<L> manager = new EventListenerManager<L>(parallel);
		
		L proxy = (L) Proxy.newProxyInstance(EventListenerManager.class.getClassLoader(), new Class[] { listenerType }, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				manager.invokeMethod(method, args);
				return null;
			}
		});
		
		manager.setProxy(proxy);
		return manager;
	}
}
