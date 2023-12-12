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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Consolidated job manager which helps in scheduling and resheduling jobs based on name.
 * @author akiran
 */
public class ConsolidatedJobManager
{
	private static Logger logger = Logger.getLogger(ConsolidatedJobManager.class.getName());
	
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
			synchronized(consolidatedJobs)
			{
				long timeLeft = getTimeLeft();
				
				if(timeLeft > 0)
				{
					threadPool.schedule(this, timeLeft, TimeUnit.MILLISECONDS);
					return;
				}

				consolidatedJobs.remove(name);
			}
			
			String threadName = Thread.currentThread().getName();
			Thread.currentThread().setName("cjob-" + name);
			
			try
			{
				runnable.run();
			} catch(Exception ex)
			{
				logger.log(Level.SEVERE, "An error occurred while executing consolidated task: " + name, ex);
			} finally
			{
				Thread.currentThread().setName(threadName);
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
		synchronized(consolidatedJobs)
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
		synchronized(consolidatedJobs)
		{
			ConsolidatedJob consolidatedJob = consolidatedJobs.get(name);
			
			if(consolidatedJob == null)
			{
				return false;
			}
			
			consolidatedJob.scheduleAfter(runnable, delay);
			return true;
		}
	}
	
	public static void scheduleJob(Runnable runnable, long fixedDelay)
	{
		Runnable wrapper = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					runnable.run();
				}catch(Exception ex)
				{
					logger.log(Level.SEVERE, "Unhandled error occurred in scheduled background job", ex);
				}
			}
		};
		
		threadPool.scheduleWithFixedDelay(wrapper, fixedDelay, fixedDelay, TimeUnit.MILLISECONDS);
	}
}
