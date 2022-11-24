package com.yukthitech.autox.common;

import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Simple count down latch implementation with callback.
 * @author akranthikiran
 */
public class AutoxCountDownLatch
{
	private AtomicInteger count;
	
	private Runnable callback;
	
	public AutoxCountDownLatch(int count)
	{
		if(count <= 0)
		{
			throw new InvalidArgumentException("Invalid count value specified: " + count);
		}
		
		this.count = new AtomicInteger(count);
	}
	
	public void setCallback(Runnable callback)
	{
		this.callback = callback;
	}
	
	public void countDown()
	{
		if(count.get() <= 0)
		{
			throw new InvalidStateException("Count down is already over");
		}
		
		int nextVal = count.decrementAndGet();
		
		if(nextVal == 0)
		{
			callback.run();
		}
	}
}
