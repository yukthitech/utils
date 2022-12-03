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
