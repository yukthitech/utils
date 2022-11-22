package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.context.AutomationContext;
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
				executor.execute(beforeChildFromParent, afterChildFromParent);
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
	
	private ExecutorService threadPool;
	
	private ExecutionPool()
	{
		String parallelExecutionCountStr = AutomationContext.getInstance().getOverridableProp(IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE);
		int parallelExecutionCount = 0;
		
		if(StringUtils.isNotBlank(parallelExecutionCountStr))
		{
			try
			{
				parallelExecutionCount = Integer.parseInt(parallelExecutionCountStr);
			}catch(Exception ex)
			{
				throw new InvalidStateException("Invalid value specified for parallel execution count config '{}'. Value specified: {}", 
						IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE, parallelExecutionCountStr, ex);
			}
			
			if(parallelExecutionCount > 0)
			{
				threadPool = Executors.newFixedThreadPool(parallelExecutionCount);
				logger.debug("Created parallel execution pool with size: {}", parallelExecutionCount);
			}
			else
			{
				logger.debug("Parallel execution is disabled as parallel pool size is specified (Config Name: {}) with zero or negative value: {}"
						, IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE, parallelExecutionCount);		
			}
		}
		else
		{
			logger.debug("Parallel execution is disabled as no parallel pool size is specified (Config Name: {})", IAutomationConstants.AUTOX_PROP_PARALLEL_POOL_SIZE);
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
			if(executor.isReadyToExecute())
			{
				executor.execute(beforeChild, afterChild);
			}
		});
	}

	private void executeParallelly(List<? extends Executor> executors, Setup beforeChildFromParent, Cleanup afterChildFromParent)
	{
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
	
	public void execute(List<? extends Executor> executors, Setup beforeChildFromParent, Cleanup afterChildFromParent, boolean parallelExecutionEnabled)
	{
		if(parallelExecutionEnabled && threadPool != null)
		{
			executeParallelly(executors, beforeChildFromParent, afterChildFromParent);
		}
		else
		{
			executeSequentially(executors, beforeChildFromParent, afterChildFromParent);
		}
	}
}
