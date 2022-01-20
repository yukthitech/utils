package com.yukthitech.autox.exec;

/**
 * Future object capable of returning result in async way.
 * @author akranthikiran
 * @param <T> type of data
 */
public class ExecutorFuture<T>
{
	/**
	 * Value of this object.
	 */
	private T value;
	
	public ExecutorFuture()
	{
	}
	
	public ExecutorFuture(T value)
	{
		this.value = value;
	}

	/**
	 * Sets the value of this object.
	 *
	 * @param value
	 *            the new value of this object
	 */
	public void setValue(T value)
	{
		this.value = value;
	}
	
	/**
	 * Gets the value of this object.
	 *
	 * @return the value of this object
	 */
	public T getValue()
	{
		return value;
	}
}
