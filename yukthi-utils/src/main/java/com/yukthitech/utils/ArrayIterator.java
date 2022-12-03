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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A iterator wrapper over array. Array can be of primitive type or complex type 
 * @author akiran
 */
public class ArrayIterator implements Iterator<Object>, Iterable<Object>
{
	private Object array;
	
	private int index = 0;
	private int length = 0;
	
	public ArrayIterator(Object array)
	{
		if(!array.getClass().isArray())
		{
			throw new IllegalArgumentException("Invalid array value specified: " + array);
		}
		
		this.array = array;
		length = Array.getLength(array);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		return (index < length);
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next()
	{
		if(index >= length)
		{
			throw new NoSuchElementException();
		}

		Object val = Array.get(array, index);
		index++;
		
		return val;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("Remove operation is not supported on this iterator");
	}

	@Override
	public Iterator<Object> iterator()
	{
		return this;
	}
}
