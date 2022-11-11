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
		private IExecutor executor;
		
		private CountDownLatch countDownLatch;
		
		private List<ExecutorRunnable> dependentItems;
		
		private ExecutorService threadPool;
		
		public ExecutorRunnable(IExecutor executor, CountDownLatch countDownLatch, ExecutorService threadPool)
		{
			this.executor = executor;
			this.countDownLatch = countDownLatch;
			this.threadPool = threadPool;
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
				executor.init();
				executor.execute();
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
	
	private void executeSequentially(List<? extends IExecutor> executors)
	{
		//Note: in sequence execution, dependencies will not be considered as ordering would be done
		// during build time
		executors.forEach(executor -> 
		{
			executor.init();
			executor.execute();
		});
	}

	private void executeParallelly(List<? extends IExecutor> executors, int parallelCount)
	{
		ExecutorService threadPool = Executors.newFixedThreadPool(parallelCount);
		CountDownLatch latch = new CountDownLatch(executors.size());
		
		IdentityHashMap<IExecutor, ExecutorRunnable> runnableMap = new IdentityHashMap<>();
		
		//create runnable objects
		for(IExecutor executor : executors)
		{
			ExecutorRunnable runnable = new ExecutorRunnable(executor, latch, threadPool);
			runnableMap.put(executor, runnable);
		}
		
		//add dependent runnables
		//NOTE: Immediate execution is not done to ensure all dependents are added properly
		List<ExecutorRunnable> directExecutables = new ArrayList<>();
		
		for(Map.Entry<IExecutor, ExecutorRunnable> entry : runnableMap.entrySet())
		{
			List<IExecutor> dependencies = entry.getKey().getDependencies();
			
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
	
	public void execute(List<? extends IExecutor> executors, int parallelCount)
	{
		if(parallelCount <= 1)
		{
			executeSequentially(executors);
		}
		else
		{
			executeParallelly(executors, parallelCount);
		}
	}
}
