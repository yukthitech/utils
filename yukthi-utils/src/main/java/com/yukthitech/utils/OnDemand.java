package com.yukthitech.utils;

import java.util.function.Supplier;

/**
 * Objects which gets populated only on demand. And based on 
 * presence of value other functions can be executed appropriately.
 */
public class OnDemand<T>
{
	/**
	 * Represents action to be executed with specified value.
	 * @param <T> Type of value
	 */
	public static interface Action<T>
	{
		public void execute(T t) throws Exception;
	}
	
	/**
	 * Represents action to be executed with NO value.
	 */
	public static interface EmptyAction
	{
		public void execute() throws Exception;
	}
	
	/**
	 * Exception thrown during error of action execution.
	 */
	public static class ActionException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;
		
		public ActionException(Exception ex)
		{
			super(ex);
		}
	}

	/**
	 * Provider which can supply value on demand.
	 */
	private Supplier<T> valueProvider;
	
	/**
	 * Value obtained, if any.
	 */
	private T value;
	
	public OnDemand(Supplier<T> valueProvider)
	{
		this.valueProvider = valueProvider;
	}
	
	/**
	 * Fetches the underlying value. If one does not exist, 
	 * value is fetched from value-provider specified during construction.
	 * @return
	 */
	public T get()
	{
		if(value == null)
		{
			value = valueProvider.get();
		}
		
		return value;
	}
	
	/**
	 * Specified action is executed if underlying value is created.
	 * @param action
	 */
	public void ifPresent(Action<T> action)
	{
		if(value == null)
		{
			return;
		}
		
		try
		{
			action.execute(value);
		}catch(Exception ex)
		{
			throw new ActionException(ex);
		}
	}
	
	/**
	 * Specified action is executed if underlying value was NEVER created.
	 * @param emptyAction
	 */
	public void ifAbsent(EmptyAction emptyAction)
	{
		try
		{
			if(value == null)
			{
				emptyAction.execute();
			}
		}catch(Exception ex)
		{
			throw new ActionException(ex);
		}
	}

	/**
	 * Appropriate action is executed based on underlying value is present or not. 
	 * @param action
	 * @param emptyAction
	 */
	public void ifPresentOrElse(Action<T> action, EmptyAction emptyAction)
	{
		try
		{
			if(value == null)
			{
				emptyAction.execute();
				return;
			}
			
			action.execute(value);
		}catch(Exception ex)
		{
			throw new ActionException(ex);
		}
	}
}
