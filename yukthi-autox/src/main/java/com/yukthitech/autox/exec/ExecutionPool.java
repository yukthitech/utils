package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executors thread pool.
 * @author akranthikiran
 */
public class ExecutionPool
{
	private static Logger logger = LogManager.getLogger(ExecutionPool.class);

	private static ExecutionPool instance = new ExecutionPool();
	
	private static class ExecutorRunnable implements Runnable
	{
		private Executor executor;
		
		private CountDownLatch countDownLatch;
		
		private List<ExecutorRunnable> dependentItems;
		
		private ExecutorService threadPool;
		
		private Setup beforeChildFromParent;
		
		private Cleanup afterChildFromParent;
		
		public ExecutorRunnable(Executor executor, CountDownLatch countDownLatch, ExecutorService threadPool, Setup beforeChildFromParent, Cleanup afterChildFromParent)
		{
			this.executor = executor;
			this.countDownLatch = countDownLatch;
			this.threadPool = threadPool;
			this.beforeChildFromParent = beforeChildFromParent;
			this.afterChildFromParent = afterChildFromParent;
		}
		
		public void addDependent(ExecutorRunnable executorRunnable)
		{
			if(this.dependentItems == null)
			{
				this.dependentItems = new ArrayList<>();
			}
			
			this.dependentItems.add(executorRunnable);
		}
		
		public void run() 
		{
			try
			{
				//if init fail, consider execution to be failed and dont invoke execute()
				if(!executor.init())
				{
					executor.execute(beforeChildFromParent, afterChildFromParent);
				}
			} catch(Exception ex)
			{
				logger.error("An error occurred while executing executor: " + executor, ex);
			} finally
			{
				countDownLatch.countDown();
			}
			
			if(CollectionUtils.isNotEmpty(dependentItems))
			{
				dependentItems.forEach(executorRunnable -> 
				{
					//NOTE: for skipped executor in all parent executors this condition will fail
					// and never gets executed
					if(executorRunnable.executor.isReadyToExecute())
					{
						threadPool.execute(executorRunnable);
					}
				});
			}
		}
	}
	
	public static ExecutionPool getInstance()
	{
		return instance;
	}
	
	private void executeSequentially(List<? extends Executor> executors, Setup beforeChild, Cleanup afterChild)
	{
		//Note: in sequence execution, dependencies will not be considered as ordering would be done
		// during build time
		executors.forEach(executor -> 
		{
			if(executor.init())
			{
				executor.execute(beforeChild, afterChild);
			}
		});
	}

	private void executeParallelly(List<? extends Executor> executors, Setup beforeChildFromParent, Cleanup afterChildFromParent, int parallelCount)
	{
		ExecutorService threadPool = Executors.newFixedThreadPool(parallelCount);
		CountDownLatch latch = new CountDownLatch(executors.size());
		
		IdentityHashMap<Executor, ExecutorRunnable> runnableMap = new IdentityHashMap<>();
		
		//create runnable objects
		for(Executor executor : executors)
		{
			ExecutorRunnable runnable = new ExecutorRunnable(executor, latch, threadPool, beforeChildFromParent, afterChildFromParent);
			runnableMap.put(executor, runnable);
		}
		
		//add dependent runnables
		//NOTE: Immediate execution is not done to ensure all dependents are added properly
		List<ExecutorRunnable> directExecutables = new ArrayList<>();
		
		for(Map.Entry<Executor, ExecutorRunnable> entry : runnableMap.entrySet())
		{
			List<Executor> dependencies = entry.getKey().getDependencies();
			
			if(CollectionUtils.isEmpty(dependencies))
			{
				directExecutables.add(entry.getValue());
				continue;
			}
			
			dependencies.forEach(dependency -> 
			{
				runnableMap.get(dependency).addDependent(entry.getValue());
			});
		}
		
		//once dependents are added, execute the executors
		directExecutables.forEach(runnable -> threadPool.execute(runnable));
		
		try
		{
			latch.await();
		} catch(InterruptedException ex)
		{
			throw new InvalidStateException("Thread was interrupted", ex);
		}
	}
	
	public void execute(List<? extends Executor> executors, Setup beforeChildFromParent, Cleanup afterChildFromParent, int parallelCount)
	{
		if(parallelCount <= 1)
		{
			executeSequentially(executors, beforeChildFromParent, afterChildFromParent);
		}
		else
		{
			executeParallelly(executors, beforeChildFromParent, afterChildFromParent, parallelCount);
		}
	}
}
