/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.utils.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yukthitech.utils.CommonUtils;

/**
 * Manager for managing listeners.
 * @author akiran
 * @param <L> listener type.
 */
public class EventListenerManager<L>
{
	private static Logger logger = Logger.getLogger(EventListenerManager.class.getName());
	
	/**
	 * Data that can be used to filter listeners before invocation.
	 * @author akiran
	 * @param <L> listener type
	 */
	public static interface ListenerFilter<L>
	{
		/**
		 * Invoked to determine the filter to be invoked or not.
		 * @param listener listener to be filtered
		 * @param data data associated with filter.
		 * @param listenerMethod method being invoked.
		 * @param params method parameters with which method is being invoked.
		 * @return true if listener method should be invoked
		 */
		public boolean filter(L listener, Object data, Method listenerMethod, Object... params);
	}
	
	/**
	 * Used when result from listeners needs to be processed.
	 * @author akiran
	 * @param <L> listener type
	 */
	public static interface ResultProcessor<L>
	{
		
		/**
		 * Invoked with result from listener.
		 *
		 * @param result the result
		 * @param listener the listener
		 * @param data the data
		 * @param listenerMethod the listener method
		 * @param params the params
		 */
		public void processResult(Object result, L listener, Object data, Method listenerMethod, Object... params);
	}
	
	/**
	 * Listener with data.
	 * @author akiran
	 */
	private class ListenerWithData
	{
		/**
		 * Listener to be invoked.
		 */
		private L listener;
		
		/**
		 * Data associated with listener.
		 */
		private Object data;

		public ListenerWithData(L listener, Object data)
		{
			this.listener = listener;
			this.data = data;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
			{
				return true;
			}

			if(!(obj instanceof EventListenerManager.ListenerWithData))
			{
				return false;
			}

			ListenerWithData other = (ListenerWithData) obj;
			return listener.equals(other.listener) && Objects.equals(data, other.data);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashcode()
		 */
		@Override
		public int hashCode()
		{
			return listener.hashCode();
		}
	}
	
	/**
	 * Event listener proxy object.
	 */
	private L proxy;
	
	/**
	 * Listeners list.
	 */
	private List<ListenerWithData> listeners = new ArrayList<ListenerWithData>();
	
	/**
	 * Thread pool for executing listeners.
	 */
	private ScheduledExecutorService threadPool;
	
	/**
	 * Flag indicating if listeners should be invoked sequentially or parallelly.
	 */
	private boolean parallel;
	
	/**
	 * Filter to be invoked to filter listeners.
	 */
	private ListenerFilter<L> filter;
	
	/**
	 * Result processor for listeners.
	 */
	private ResultProcessor<L> resultProcessor;
	
	private EventListenerManager(boolean parallel, int poolSize)
	{
		if(parallel)
		{
			threadPool = Executors.newScheduledThreadPool(poolSize);
		}
	}
	
	/**
	 * Sets the filter to be invoked to filter listeners.
	 *
	 * @param filter the new filter to be invoked to filter listeners
	 */
	public void setFilter(ListenerFilter<L> filter)
	{
		this.filter = filter;
	}
	
	/**
	 * Sets the result processor for listeners.
	 *
	 * @param resultProcessor the new result processor for listeners
	 */
	public void setResultProcessor(ResultProcessor<L> resultProcessor)
	{
		this.resultProcessor = resultProcessor;
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
		addListener(listener, null);
	}
	
	/**
	 * Adds the specified listener.
	 * @param listener listener to add.
	 * @param data data for listener, which can be used for filtering.
	 */
	public void addListener(L listener, Object data)
	{
		if(listener == null)
		{
			throw new NullPointerException("Listener can not be null.");
		}
		
		listeners.add(new ListenerWithData(listener, data));
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener
	 * @return true if listener is found and removed.
	 */
	public boolean removeListener(L listener)
	{
		return listeners.remove(new ListenerWithData(listener, null));
	}
	
	public boolean removeListener(L listener, Object data)
	{
		return listeners.remove(new ListenerWithData(listener, data));
	}

	/**
	 * Fetches the listeners added to this manager.
	 * @return
	 */
	public Collection<L> getListeners()
	{
		List<L> listeners = new ArrayList<L>();
		
		for(ListenerWithData ldata : this.listeners)
		{
			listeners.add(ldata.listener);
		}
		
		return listeners;
	}
	
	/**
	 * Invokes specified method on specified listener with specified params.
	 * @param listener listener to be invoked
	 * @param method method to be invoked
	 * @param params params to be passed
	 */
	private void invokeListener(ListenerWithData listener, Method method, Object... params)
	{
		if(filter != null && !filter.filter(listener.listener, listener.data, method, params))
		{
			return;
		}
		
		Object result = null;
		
		try
		{
			result = method.invoke(listener.listener, params);

			if(resultProcessor != null)
			{
				resultProcessor.processResult(result, listener.listener, listener.data, method, params);
			}
		}catch(Exception ex)
		{
			logger.log(Level.SEVERE, "An error occurred while while invoking listener or result processing - " + listener, ex);
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
		List<ListenerWithData> listeners = new ArrayList<ListenerWithData>( this.listeners );
		
		for(ListenerWithData listener : listeners)
		{
			if(!parallel)
			{
				invokeListener(listener, method, params);
				continue;
			}
			
			final ListenerWithData finalListener = listener;
			
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
	public static <L> EventListenerManager<L> newEventListenerManager(Class<L> listenerType, boolean parallel)
	{
		return newEventListenerManager(listenerType, parallel, 10);
	}
	
	/**
	 * Factory method to create event listener manager.
	 *
	 * @param <L> the generic type
	 * @param listenerType the listener type
	 * @param parallel the parallel
	 * @param threadPoolSize the thread pool size
	 * @return the event listener manager
	 */
	@SuppressWarnings("unchecked")
	public static <L> EventListenerManager<L> newEventListenerManager(Class<L> listenerType, boolean parallel, int threadPoolSize)
	{
		final EventListenerManager<L> manager = new EventListenerManager<L>(parallel, threadPoolSize);
		
		L proxy = (L) Proxy.newProxyInstance(EventListenerManager.class.getClassLoader(), new Class[] { listenerType }, new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				manager.invokeMethod(method, args);
				return CommonUtils.getDefaultValue(method.getReturnType());
			}
		});
		
		manager.setProxy(proxy);
		return manager;
	}
}
