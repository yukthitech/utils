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
package com.yukthitech.utils.pool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maintains the pool of objects
 * 
 * @author akiran
 */
public class ObjectPool<T>
{
	private static Logger logger = Logger.getLogger(ObjectPool.class.getName());
	
	private List<T> freeObjects = new LinkedList<T>();
	private Set<T> usedObjects = new HashSet<T>();
	
	private ReentrantLock poolLock = new ReentrantLock();
	private Condition OBJECT_AVAILALBE = poolLock.newCondition();
	
	private Class<T> type;
	private int poolSize = 0;
	private int poolSizeLimit = Integer.MAX_VALUE;
	
	/**
	 * Creates object pool of specified type and limit
	 * @param type Type of object to be maintained in pool
	 * @param limit Limit of pool size
	 */
	public ObjectPool(Class<T> type, int limit)
	{
		if(limit <= 0)
		{
			throw new IllegalArgumentException("Pool size limit should be non-zero positive value - " + limit);
		}
		
		this.type = type;
		this.poolSizeLimit = limit;
	}

	
	/**
	 * Equivalent to call {@link #ObjectPool(Class, int) ObjectPool(type, Integer.MAX_VALUE)}
	 * @param type
	 */
	public ObjectPool(Class<T> type)
	{
		this(type, Integer.MAX_VALUE);
	}
	
	@SuppressWarnings("unchecked")
	public ObjectPool(Class<T> type, T... initObjects)
	{
		if(initObjects == null || initObjects.length == 0)
		{
			throw new NullPointerException("No init objects specified for pooling.");
		}
		
		this.type = type;
		this.poolSizeLimit = 0;
		addToPool(initObjects);
	}
	
	@SuppressWarnings("unchecked")
	public void addToPool(T... objects)
	{
		poolLock.lock();
		
		try
		{
			for(T object : objects)
			{
				this.freeObjects.add(object);
				this.poolSizeLimit++;
			}
		}finally
		{
			poolLock.unlock();
		}
	}

	/**
	 * Creates and return new object of underlying type. 
	 * 
	 * @return
	 */
	private T newObject()
	{
		try
		{
			T inst = type.newInstance();
			poolSize++;
			
			return inst;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating instance of type: " + type.getName(), ex);
		}
	}
	
	/**
	 * Gets object from the pool. If no new object is available, new object is created (in specified pool limit)
	 * 
	 * If pool size limit is reached, returns null
	 * @return
	 */
	public T getObject()
	{
		try
		{
			return getObject(false);
		}catch(InterruptedException ex)
		{
			//this is not suppose to happen
			throw new IllegalStateException("Unexpected error occurred", ex);
		}
	}
	
	/**
	 * Gets object from the pool. If no new object is available, new object is created (in specified pool limit)
	 * 
	 * If pool size limit is reached, waits till object is available
	 * @return
	 */
	public T getObjectWithWait() throws InterruptedException
	{
		return getObject(true);
	}
	
	/**
	 * @param wait
	 * @return
	 * @throws InterruptedException
	 */
	private T getObject(boolean wait) throws InterruptedException
	{
		poolLock.lock();
		
		try
		{
			T inst = null;
			
			//if the object is not available
			if(freeObjects.isEmpty())
			{
				//if pool is within limits
				if(poolSize < poolSizeLimit)
				{
					inst = newObject();
				}
				//if pool limit is already reached
				else
				{
					//if wait is enabled
					if(wait)
					{
						logger.log(Level.FINEST, "Wait for availability of free object");
						
						//wait till object is available
						while(freeObjects.isEmpty())
						{
							OBJECT_AVAILALBE.await();
						}
						
						inst = freeObjects.remove(0);
					}
					//if wait is not enabled
					else
					{
						return null;
					}
				}
			}
			//if object is available
			else
			{
				inst = freeObjects.remove(0);
			}
			
			//add the object to used objects
			usedObjects.add(inst);
			return inst;
		}finally
		{
			poolLock.unlock();
		}
	}

	/**
	 * Frees the specified object in the pool
	 * @param object
	 * @return True if the object was in used state and it is freed successfully
	 */
	public boolean freeObject(T object)
	{
		poolLock.lock();
		
		try
		{
			//remove from used objects
			if(usedObjects.remove(object))
			{
				//add to free list
				freeObjects.add(object);
				return true;
			}
			
			return false;
		}finally
		{
			poolLock.unlock();
		}
	}
}
