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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.test.ITestGroups;

/**
 * 
 * @author akiran
 */
public class TObjectLockManager
{
	private class LockThread extends Thread
	{
		private boolean tryingToLock = false;
		private boolean lockObtained = false;
		private boolean lockReleased = false;
		private boolean lockError = false;

		private boolean releaseRequested = false;
		
		private Object forLock = new Object();
		
		public LockThread(Object forLock, String name)
		{
			super(name);
			
			this.forLock = forLock;
		}
		
		public void run()
		{
			tryingToLock = true;
			
			try
			{
				objectLockManager.lockObject(forLock);
			}catch(Exception e)
			{
				lockError = true;
				return;
			}
			
			lockObtained = true;
			
			try
			{
				while(!releaseRequested)
				{
					try
					{
						Thread.sleep(10);
					}catch(Exception ex)
					{}
				}
			}finally
			{
				objectLockManager.releaseObject(forLock);
				lockReleased = true;
			}
		}
		
		public void waitTillLocked()
		{
			while(!lockObtained)
			{
				try
				{
					Thread.sleep(10);
				}catch(Exception ex)
				{}
			}
		}
		
		public void waitTillReleased()
		{
			this.releaseRequested = true;
			
			while(!lockReleased)
			{
				try
				{
					Thread.sleep(10);
				}catch(Exception ex)
				{}
			}
		}
	}
	
	private Object forLock = new Object();
	private ObjectLockManager objectLockManager = new ObjectLockManager();

	/**
	 * Test simple lock and release works properly
	 * @throws Exception
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testSimpleLockAndRelease() throws Exception
	{
		//make sure lock is free
		Assert.assertFalse(objectLockManager.isObjectLocked(forLock));
		
		LockThread thread = new LockThread(forLock, "LockThread1");
		thread.start();
		
		thread.waitTillLocked();
		
		//wait for sometime
		Thread.sleep(1000);
		
		//make sure lock is not released
		Assert.assertTrue(objectLockManager.isObjectLocked(forLock));
		
		//request for release
		thread.waitTillReleased();
		
		//make sure lock is release
		Assert.assertFalse(objectLockManager.isObjectLocked(forLock));
		
		Assert.assertEquals(objectLockManager.getLockCount(), 0, "Some locks are not released.");
	}

	/**
	 * Tests multiple threads using same lock are synchronized
	 * @throws Exception
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testThreadsSynchronization() throws Exception
	{
		//start thread1 and make sure it got the lock
		LockThread thread1 = new LockThread(forLock, "LockThread2");
		thread1.start();
		thread1.waitTillLocked();
		
		//start thread2 and wait for sometime and check if it is able to obtain the lock
		LockThread thread2 = new LockThread(forLock, "LockThread3");
		thread2.start();
		
		Thread.sleep(1000);
		
		Assert.assertTrue(thread2.tryingToLock);
		Assert.assertFalse(thread2.lockObtained);
		Assert.assertTrue(objectLockManager.isObjectLocked(forLock));
		
		//release the lock by thread1 and ensure thread2 is able to get the lock
		thread1.waitTillReleased();
		Thread.sleep(500);
		
		Assert.assertTrue(thread2.lockObtained);
		Assert.assertTrue(objectLockManager.isObjectLocked(forLock));
		
		//release the lock by thread2 and ensure lock is release properly
		thread2.waitTillReleased();
		
		Assert.assertFalse(objectLockManager.isObjectLocked(forLock));
		
		Assert.assertEquals(objectLockManager.getLockCount(), 0, "Some locks are not released.");
	}
	
	/**
	 * Tests only required number of conditions are used and pooled properly
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testConditionPoolUsage()
	{
		Object lock1 = new Object();
		Object lock2 = new Object();
		Object lock3 = new Object();
		
		//start 2 threads without releasing locks and check number of conditions used
		LockThread thread1 = new LockThread(lock1, "LockThread4");
		thread1.start();
		thread1.waitTillLocked();
		
		LockThread thread2 = new LockThread(lock2, "LockThread5");
		thread2.start();
		thread2.waitTillLocked();
		
		Assert.assertEquals(objectLockManager.getMaxLocksUsed(), 2);
		
		//release one of the lock
		thread2.waitTillReleased();
		
		Assert.assertEquals(objectLockManager.getMaxLocksUsed(), 2);
		
		//now create new lock by new thread and no new condition is created
		LockThread thread3 = new LockThread(lock3, "LockThread6");
		thread3.start();
		thread3.waitTillLocked();
		
		Assert.assertEquals(objectLockManager.getMaxLocksUsed(), 2);
		
		//release all locks
		thread1.waitTillReleased();
		thread3.waitTillReleased();
		
		Assert.assertEquals(objectLockManager.getLockCount(), 0, "Some locks are not released.");
	}
	
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = IllegalStateException.class)
	public void testReleaseWithoutLock()
	{
		Object someLock = new Object();
		objectLockManager.releaseObject(someLock);
		
		Assert.assertEquals(objectLockManager.getLockCount(), 0, "Some locks are not released.");
	}	
	
	/**
	 * Tests waiting thread is interrupted
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testWithInterrupution() throws Exception
	{
		//start 2 threads with same lock
		LockThread thread1 = new LockThread(forLock, "LockThread7");
		thread1.start();
		thread1.waitTillLocked();
		
		LockThread thread2 = new LockThread(forLock, "LockThread8");
		thread2.start();

		Thread.sleep(100);
		
		//ensure thread1 got the lock and thread2 is waiting
		Assert.assertTrue(thread1.lockObtained);
		Assert.assertFalse(thread2.lockObtained);

		//interrupt second thread and ensure it comes out of wait and lock is not obtained
		thread2.interrupt();
		Thread.sleep(100);
		
		Assert.assertTrue(thread2.lockError);
		Assert.assertFalse(thread1.lockReleased);
		
		//release locks
		thread1.waitTillReleased();
		
		Assert.assertEquals(objectLockManager.getLockCount(), 0, "Some locks are not released.");
	}
	
	/**
	 * Tests that multiple locking on same object is not creating any problem
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testLockMultipleTimes() throws Exception
	{
		for(int i = 0; i < 100; i++)
		{
			String s = new String("testObject");
			
			objectLockManager.lockObject(s);
			
			try
			{
				System.out.println("Locked: " + i);
				Thread.sleep(10);
			}finally
			{
				objectLockManager.releaseObject(s);
			}
		}
	}
	
	/**
	 * Ensures relocking by same thread works.
	 * @throws Exception
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testRelocking() throws Exception
	{
		String lockObject = new String("lockForRelock");
		
		objectLockManager.lockObject(lockObject);
		
		try
		{
			System.out.println("Got first lock...");
			
			objectLockManager.lockObject(lockObject);
			
			try
			{
				System.out.println("Second level lock...");
			}finally
			{
				objectLockManager.releaseObject(lockObject);
			}
		}finally
		{
			objectLockManager.releaseObject(lockObject);
		}
	}
}
