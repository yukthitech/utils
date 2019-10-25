package com.yukthitech.utils.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.ObjectLockManager;

/**
 * Consolidated job manager which helps in scheduling and resheduling jobs based on name.
 * @author akiran
 */
public class ConsolidatedJobManager
{
	private static Logger logger = LogManager.getLogger(ConsolidatedJobManager.class);
	
	private static ObjectLockManager jobNameLocker = new ObjectLockManager();
	
	/**
	 * Job whose multiple invocation has to be consolidated and executed only once.
	 * @author akiran
	 */
	private static class ConsolidatedJob implements Runnable
	{
		private String name;
		
		/**
		 * Job to be executed.
		 */
		private Runnable runnable;
		
		/**
		 * Time at which this job should be executed.
		 */
		private long scheduledAt;

		public ConsolidatedJob(String name, Runnable runnable, long delay)
		{
			this.name = name;
			this.runnable = runnable;
			this.scheduledAt = System.currentTimeMillis() + delay;
		}
		
		/**
		 * To be invoked when already existing job is rescheduled.
		 * @param runnable
		 * @param delay
		 */
		public void scheduleAfter(Runnable runnable, long delay)
		{
			this.runnable = runnable;
			scheduledAt = System.currentTimeMillis() + delay;
		}
		
		/**
		 * Tells how much time is left for execution.
		 * @return
		 */
		public long getTimeLeft()
		{
			return scheduledAt - System.currentTimeMillis();
		}
		
		@Override
		public void run()
		{
			jobNameLocker.lockObject(name);
			
			String threadName = Thread.currentThread().getName();
			Thread.currentThread().setName("cjob-" + name);
			
			try
			{
				long timeLeft = getTimeLeft();
				
				if(timeLeft > 0)
				{
					threadPool.schedule(this, timeLeft, TimeUnit.MILLISECONDS);
					return;
				}
				
				consolidatedJobs.remove(name);
				runnable.run();
			} catch(Exception ex)
			{
				logger.error("An error occurred while executing consolidated task: {}", name, ex);
			} finally
			{
				Thread.currentThread().setName(threadName);
				jobNameLocker.releaseObject(name);
			}
		}
	}
	
	/**
	 * Thread pools for scheduled jobs.
	 */
	private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
	
	private static Map<String, ConsolidatedJob> consolidatedJobs = new HashMap<String, ConsolidatedJob>();

	/**
	 * Executes specified runnable task after specified delay.
	 * @param runnable
	 * @param delay
	 */
	public static void execute(Runnable runnable, long delay)
	{
		threadPool.schedule(runnable, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Schedules specified runnable job after specified delay. 
	 * 
	 * If the job was already scheduled, the current schedule
	 * will be cancelled and will be scheduled after specified delay from current time. And new runnable will replace old runnable.
	 * 
	 * @param name Name of the job
	 * @param runnable job to execute
	 * @param delay delay after which job needs to be scheduled.
	 */
	public static void executeConsolidatedJob(String name, Runnable runnable, long delay)
	{
		jobNameLocker.lockObject(name);
		
		try
		{
			synchronized(ConsolidatedJobManager.class)
			{
				ConsolidatedJob consolidatedJob = consolidatedJobs.get(name);
				
				if(consolidatedJob != null)
				{
					consolidatedJob.scheduleAfter(runnable, delay);
					return;
				}
				
				consolidatedJob = new ConsolidatedJob(name, runnable, delay);
				consolidatedJobs.put(name, consolidatedJob);
				
				threadPool.schedule(consolidatedJob, delay, TimeUnit.MILLISECONDS);
			}
		}finally
		{
			jobNameLocker.releaseObject(name);
		}
	}
	
	/**
	 * Reschedules specified job after specified delay, if job was already scheduled and not yet completed.  And new runnable will replace old runnable.
	 * If specified job is not found, false will be returned. 
	 * 
	 * @param name Name of the job
	 * @param runnable job to execute
	 * @param delay delay after which job needs to be scheduled.
	 * @return true if reschedule was successful
	 */
	public static boolean rescheduleConsolidatedJob(String name, Runnable runnable, long delay)
	{
		jobNameLocker.lockObject(name);
		
		try
		{
			synchronized(ConsolidatedJobManager.class)
			{
				ConsolidatedJob consolidatedJob = consolidatedJobs.get(name);
				
				if(consolidatedJob == null)
				{
					return false;
				}
				
				consolidatedJob.scheduleAfter(runnable, delay);
				return true;
			}
		}finally
		{
			jobNameLocker.releaseObject(name);
		}
	}
}
