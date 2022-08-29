package com.yukthitech.utils;

import java.util.function.Supplier;

import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Value which gets cached for specified amount of time
 * and then later fetches the value again from source.
 * 
 * @author akranthikiran
 */
public class TimedValue<V>
{
	/**
	 * Cache time after which the value gets refreshed.
	 */
	private long cacheTimeMillis;
	
	/**
	 * Source from which value can be reobtained.
	 */
	private Supplier<V> source;
	
	/**
	 * Cached value.
	 */
	private V value;
	
	/**
	 * Time when current value was fetched from source.
	 */
	private long valueTime = 0;

	public TimedValue(long cacheTimeMillis, Supplier<V> source)
	{
		if(cacheTimeMillis <= 0)
		{
			throw new InvalidArgumentException("Cache-time should be positive number: {}", cacheTimeMillis);
		}
		
		if(source == null)
		{
			throw new NullPointerException("Source can not be null");
		}
		
		this.cacheTimeMillis = cacheTimeMillis;
		this.source = source;
	}
	
	/**
	 * Fetches the underlying value from cache or source.
	 * @return
	 */
	public synchronized V get()
	{
		long curTime = System.currentTimeMillis();
		long diff = curTime - valueTime;
		
		if(diff < cacheTimeMillis)
		{
			return value;
		}
		
		value = source.get();
		valueTime = curTime;
		
		return value;
	}
}
