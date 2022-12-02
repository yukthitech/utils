package com.yukthitech.autox.debug.server;

/**
 * Synchronization aid which can help in waiting for an event to occur.
 * And reuse the lock and wait for event.
 *  
 * @author akranthikiran
 */
public class EventWaitLock
{
	private Object lock = new Object();
	
	public void waitForEvent() throws InterruptedException
	{
		synchronized(lock)
		{
			lock.wait();
		}
		
		System.out.println("Lock is released...");
	}
	
	public void notifyEvent()
	{
		System.out.println("Notifyin lock...");
		synchronized(lock)
		{
			lock.notifyAll();
		}
	}
}
