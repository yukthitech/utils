/**
 * 
 */
package com.yukthitech.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Utility class which helps in locking objects till they are released. This class will
 * ensure an object is locked at a time by single thread. Other thread which tries to lock
 * same object will wait till lock is obtained.
 * 
 * This is done by using single ReentrantLock and pool of conditions.
 * 
 * Please note. If same thread tries to acquire lock on same object twice, there will be dead lock.
 * 
 * @author akiran
 */
public class ObjectLockManager
{
	private static Logger logger = LogManager.getLogger(ObjectLockManager.class);
	
	private class ObjectLock extends ReentrantLock
	{
		private static final long serialVersionUID = 1L;
		private AtomicInteger counter = new AtomicInteger(0);
		
		public void lockObject(Object object)
		{
			try
			{
				super.lockInterruptibly();
			}catch(InterruptedException ex)
			{
				throw new RuntimeInterruptedException(ex);
			}
			
			counter.incrementAndGet();
			
			mainLock.lock();
			
			try
			{
				objectToLocks.put(object, this);
			}finally
			{
				mainLock.unlock();
			}
		}
		
		public void releaseObject(Object object)
		{
			int val = counter.decrementAndGet();
			
			if(val > 0)
			{
				super.unlock();
				return;
			}

			mainLock.lock();
			
			try
			{
				objectToLocks.remove(object);
				lockPool.add(this);
			}finally
			{
				mainLock.unlock();
			}
			
			super.unlock();
		}
	}
	
	/**
	 * Lock to synchronize the threads will obtaining object lock
	 */
	private ReentrantLock mainLock = new ReentrantLock();
	
	/**
	 * Maintains the objects that are locked and their corresponding locks. Locks are used to 
	 * notify other waiting threads for same object lock.
	 */
	private Map<Object, ObjectLock> objectToLocks = new HashMap<Object, ObjectLock>();
	
	/**
	 * Pool of locks. So that lock objects can be reused
	 */
	private List<ObjectLock> lockPool = new ArrayList<ObjectLock>();
	
	/**
	 * Keeps track maximum pool size that was used during this lock manager
	 * life
	 */
	private AtomicInteger maxLocksUsed = new AtomicInteger(0);
	
	public boolean isObjectLocked(Object object)
	{
		mainLock.lock();
		
		try
		{
			return  (objectToLocks.get(object) != null);
		}finally
		{
			mainLock.unlock();
		}
	}

	public ObjectLock getLockOf(Object object)
	{
		mainLock.lock();
		
		try
		{
			return objectToLocks.get(object);
		}finally
		{
			mainLock.unlock();
		}
	}
	
	public ObjectLock getFreeLock()
	{
		ObjectLock objLock = null;

		mainLock.lock();
		
		try
		{
			//if no locks are available in pool
			if(lockPool.isEmpty())
			{
				//create new lock
				objLock = new ObjectLock();
				maxLocksUsed.incrementAndGet();
			}
			//if pool has locks
			else
			{
				//reuse the lock from pool
				objLock = lockPool.remove(lockPool.size() - 1);
			}
			
			return objLock;
		}finally
		{
			mainLock.unlock();
		}
	}

	/**
	 * Locks the specified "object" till {@link #releaseObject(Object)} is called with same object. Other threads
	 * which tries to obtain lock on same object, will wait till lock is obtained
	 * @param object
	 */
	public void lockObject(Object object)
	{
		//check if current object is already locked
		ObjectLock objLock = getLockOf(object);

		//if locked wait for lock to be released
		if(objLock != null)
		{
			logger.debug("Waiting for lock on object using same lock - {}", object);
			
			objLock.lockObject(object);
			return;
		}
		
		objLock = getFreeLock();
		objLock.lockObject(object);
	}
	
	/**
	 * Releases the lock obtained on specified "object" by {@link #lockObject(Object)}. This will signal
	 * all the other threads waiting for locking this object. 
	 * @param object
	 */
	public void releaseObject(Object object)
	{
		ObjectLock objLock = getLockOf(object);

		//if the object was not locked
		if(objLock == null)
		{
			throw new IllegalStateException("Specified object is not locked by this manager - " + object);
		}
		
		objLock.releaseObject(object);
	}
	
	/**
	 * Gets maxumum number of conditions used by this manager
	 * @return the maxConditionsUsed
	 */
	public int getMaxLocksUsed()
	{
		return maxLocksUsed.get();
	}
	
	/**
	 * Obtains number of locks controlled by this manager currently.
	 * @return
	 */
	public int getLockCount()
	{
		mainLock.lock();
		
		try
		{
			return objectToLocks.size();
		}finally
		{
			mainLock.unlock();
		}
	}
}
