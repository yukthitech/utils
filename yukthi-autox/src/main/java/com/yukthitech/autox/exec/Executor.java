package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ErrorDetails;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.exec.report.ReportDataManager;
import com.yukthitech.autox.test.AutoxException;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.ExpectedException;
import com.yukthitech.autox.test.Function;
import com.yukthitech.autox.test.Setup;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.TestStatus;
import com.yukthitech.autox.test.lang.steps.LangException;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

public abstract class Executor
{
	private static Logger logger = LogManager.getLogger(Executor.class);
	
	/**
	 * Unique id of this executor for ease of logging and debugging.
	 */
	protected String uniqueId;
	
	private ReportDataManager reportManager = ReportDataManager.getInstance();
	
	protected Object executable;
	
	protected Setup setup;
	
	protected Cleanup cleanup;
	
	protected Setup beforeChild;
	
	protected Cleanup afterChild;

	protected boolean parallelExecutionEnabled;

	private List<Executor> childExecutors;
	
	protected List<IStep> childSteps;
	
	private Executor parentExecutor;
	
	protected TestStatus status;
	
	protected String statusMessage;
	
	protected ExpectedException expectedException;
	
	private String childName;
	
	/**
	 * Flag to be set, when child status(es) has to be considered along
	 * with current executor.
	 */
	protected boolean includeChildStauts;
	
	/**
	 * Flag indicating that this executor should share
	 * context (attributes) with parent context.
	 */
	protected boolean parentContextShared;
	
	protected Executor(Object executable, String childName)
	{
		this.executable = executable;
		this.childName = childName != null ? childName : "Execution";
		
		this.uniqueId = reportManager.getRep(this);
	}
	
	public String getUniqueId()
	{
		return uniqueId;
	}
	
	/**
	 * Invoked before execution. If this method returns null execution occurs immediately.
	 * If other executors are returned, then this method will be invoked again post execution
	 * of each of specified executor.
	 *  
	 * @return Dependency executor for which executor is waiting for. Null if this executor is ready
	 * to execute.
	 */
	public List<Executor> getDependencies()
	{
		return null;
	}
	
	/**
	 * Invoked to check if all required dependencies are executed.
	 * @return
	 */
	public boolean isReadyToExecute()
	{
		return true;
	}
	
	public boolean isParentContextShared()
	{
		return parentContextShared;
	}
	
	/**
	 * This is invoked once all dependencies (if any) are completed and this executor is ready to execute.
	 * In this method data-provider execution (and child population) should occur.
	 */
	protected boolean init()
	{
		return true;
	}
	
	public boolean isStarted()
	{
		return (status != null);
	}
	
	public Object getExecutable()
	{
		return executable;
	}
	
	public Executor getParentExecutor()
	{
		return parentExecutor;
	}
	
	protected void addChildExector(Executor executor)
	{
		if(this.childExecutors == null)
		{
			this.childExecutors = new ArrayList<>();
		}
		
		executor.parentExecutor = this;
		this.childExecutors.add(executor);
	}
	
	public List<Executor> getChildExecutors()
	{
		return childExecutors;
	}
	
	protected void preexecute()
	{}
	
	protected void postExecute()
	{}

	public void execute(Setup beforeChildFromParent, Cleanup afterChildFromParent, AsyncTryCatchBlock parent)
	{
		synchronized(this)
		{
			//ensure because of multi threading, multiple executions does not happen
			//  this usecase is more feasible in case of dependent test cases
			if(status != null)
			{
				return;
			}
			
			status = TestStatus.IN_PROGRESS;
		}

		ExecutionContextManager.executeInContext(this, () -> 
		{
			reportManager.executionStarted(ExecutionType.MAIN, this);
		});
		
		
		ObjectWrapper<Boolean> beforeChildCompleted = new ObjectWrapper<>(false);
			
		AsyncTryCatchBlock.doTry(uniqueId, mainCallback -> 
		{
			boolean exeRes = ExecutionContextManager.executeInContext(Executor.this, () -> 
			{
				if(!ExecutorUtils.executeSetup(beforeChildFromParent, "Before-Child", this))
				{
					return false;
				}
				
				beforeChildCompleted.setValue(true);
	
				preexecute();
	
				//Execute setup
				if(!ExecutorUtils.executeSetup(setup, "Setup", Executor.this))
				{
					return false;
				}
				
				if(!init())
				{
					return false;
				}
				
				return true;
			});
			
			if(!exeRes)
			{
				return;
			}
			
			mainCallback.newChild(uniqueId + "-childExecution", childCallback -> 
			{
				//execute children
				if(childExecutors != null)
				{
					executeChildExecutors(childCallback);
				}
				else
				{
					executeChildSteps();
				}
			}).onError((childCallback, ex) -> 
			{
				logger.error("An error occurred during execution", ex);
				throw ex;
			}).onFinally(childCallback -> 
			{
				ExecutionContextManager.executeInContext(Executor.this, () -> 
				{
					//Pre cleanup is expected to take care of tasks like data-clean-up
					preCleanup();
					
					//execute cleanup
					ExecutorUtils.executeCleanup(cleanup, "Cleanup", this);
				});
			});
			
		}).onFinally(mainCallback ->
		{
			ExecutionContextManager.executeInContext(Executor.this, () -> 
			{
				//execute after child even if setup of test case fails
				// but should not execute if before-child fails
				if(beforeChildCompleted.getValue())
				{
					ExecutorUtils.executeCleanup(afterChildFromParent, "After-Child", this);
					postExecute();
				}
				
				closeReportManager();
			});
		}).executeWithParent(parent);

		/*
		try
		{
			//Execute setup
			if(!ExecutorUtils.executeSetup(setup, "Setup", this))
			{
				return;
			}
			
			if(!init())
			{
				return;
			}
			
			try
			{
				//execute children
				if(childExecutors != null)
				{
					executeChildExecutors();
				}
				else
				{
					executeChildSteps();
				}
			} catch(RuntimeException ex) 
			{
				logger.error("An error occurred during exection", ex);
				throw ex;
			} finally
			{
				//Pre cleanup is expected to take care of tasks like data-clean-up
				preCleanup();
				
				//execute cleanup
				ExecutorUtils.executeCleanup(cleanup, "Cleanup", this);
			}
		} finally
		{
			//execute after child even if setup of test case fails
			// but should not execute if before-child fails
			ExecutorUtils.executeCleanup(afterChildFromParent, "After-Child", this);
			
			postExecute();
			closeReportManager();
			ExecutionContextManager.getInstance().pop(this);
		}
		*/
	}
	
	protected void preCleanup()
	{}
	
	protected void setStatus(TestStatus status, String mssg)
	{
		this.status = status;
		this.statusMessage = mssg;
	}
	
	private void closeReportManager()
	{
		if(status == TestStatus.ERRORED)
		{
			reportManager.executionErrored(ExecutionType.MAIN, this, statusMessage);
		}
		else if(status == TestStatus.FAILED)
		{
			reportManager.executionFailed(ExecutionType.MAIN, this, statusMessage);
		}
		else if(status == TestStatus.SKIPPED)
		{
			reportManager.executionSkipped(ExecutionType.MAIN, this, statusMessage);
		}
		else
		{
			reportManager.executionCompleted(ExecutionType.MAIN, this);
		}
	}
	
	private void getStatusDetails(StatusTracker tracker, List<Executor> childExecutors)
	{
		for(Executor cexecutor : childExecutors)
		{
			if(cexecutor.includeChildStauts && CollectionUtils.isNotEmpty(cexecutor.childExecutors))
			{
				getStatusDetails(tracker, cexecutor.childExecutors);
				continue;
			}

			if(cexecutor.status == TestStatus.ERRORED)
			{
				tracker.errorCount++;
			}
			else if(cexecutor.status == TestStatus.FAILED)
			{
				tracker.failureCount++;
			}
			else if(cexecutor.status == TestStatus.SKIPPED)
			{
				tracker.skipCount++;
			}
			
			tracker.status = TestStatus.getEffectiveStatus(tracker.status, cexecutor.status);
		}
	}

	private void executeChildExecutors(AsyncTryCatchBlock parent)
	{
		Thread currentThread = Thread.currentThread();
		String actName = currentThread.getName();

		parent
			.newChild(uniqueId + "-subexecutors", callback -> 
			{
				//currentThread.setName(uniqueId);
				ExecutionPool.getInstance().execute(this, childExecutors, beforeChild, afterChild, parallelExecutionEnabled, callback);
			}).onComplete(subtaskCallback -> 
			{
				StatusTracker statusTracker = new StatusTracker();
				getStatusDetails(statusTracker, childExecutors);
				
				if(statusTracker.status == TestStatus.SUCCESSFUL)
				{
					setStatus(TestStatus.SUCCESSFUL, null);
					return;
				}
	
				StringBuilder mssg = new StringBuilder();
				
				if(statusTracker.errorCount > 0)
				{
					mssg.append("Errored");
				}
				
				if(statusTracker.failureCount > 0)
				{
					mssg.append(mssg.length() > 0 ? " / " : "");
					mssg.append("Failed");
				}
				
				if(statusTracker.skipCount > 0)
				{
					mssg.append(mssg.length() > 0 ? " / " : "");
					mssg.append("Skipped");
				}
				
				mssg.insert(0, "One or more " + childName + "(s) ");
	
				setStatus(statusTracker.status, mssg.toString());
			}).onFinally(callback -> 
			{
				Thread.currentThread().setName(actName);
			});
	}
	
	private void executeChildSteps()
	{
		ExecutionContextManager.executeInContext(this, () -> 
		{
			IExecutionLogger logger = ReportDataManager.getInstance().getExecutionLogger(this);
			ObjectWrapper<IStep> currentStep = new ObjectWrapper<>();
	
			//NOTE: Even parallel execution is used by test-suites or data-test-cases, final test case
			// execution happens with child steps which should happen here. Hence exception handling is done
			// here only
			try
			{
				StepsExecutor.execute(logger, childSteps, currentStep);
				setStatus(TestStatus.SUCCESSFUL, null);
			} catch(Exception ex)
			{
				handleException(currentStep.getValue(), logger, ex);
			}
		});
	}
	
	private void handleException(IStep step, IExecutionLogger exeLogger, Exception ex)
	{
		//from exception, try to find the step which caused the problem
		//	so that approp plugin handlers can be called.
		if(ex instanceof AutoxException)
		{
			AutoxException autoxException = (AutoxException) ex;
			IStep srcStep = autoxException.getSourceStep();
			
			if(srcStep != null)
			{
				logger.info("As the exception '{}' is caused by step '{}', considerting this step instead of input step: {}", ex, srcStep, step);
				step = srcStep;
			}
		}
		
		Executable executable = (step instanceof Function) ?  ExecutorUtils.createExecutable((Function) step) : step.getClass().getAnnotation(Executable.class);
		String name = executable.name();
		
		String stepType = (step instanceof IValidation) ? "Validation" : "Step";
		
		if(ex instanceof AutoxValidationException)
		{
			ExecutorUtils.invokeErrorHandling(executable, new ErrorDetails(exeLogger, step, ex));
			setStatus(TestStatus.FAILED, ex.getMessage());
			return;
		}
		
		if((ex instanceof TestCaseFailedException) || (ex instanceof LangException))
		{
			ExecutorUtils.invokeErrorHandling(executable, new ErrorDetails(exeLogger, step, ex));
			setStatus(TestStatus.ERRORED, String.format("%s (%s) Errored: %s", stepType, name, ex.getMessage()));
			return;
		}
		
		if(expectedException != null)
		{
			ExpectedException expectedException = AutomationUtils.replaceExpressions("expected-exception", 
					AutomationContext.getInstance(), 
					SerializationUtils.clone(this.expectedException));
			try
			{
				expectedException.validateMatch(ex);
				
				exeLogger.debug("Expected exception occurred: {}", ex);
				setStatus(TestStatus.SUCCESSFUL, null);
				return;
			}catch(InvalidArgumentException iex)
			{
				exeLogger.error(iex.getMessage(), iex);
				ExecutorUtils.invokeErrorHandling(executable, new ErrorDetails(exeLogger, step, ex));
				
				setStatus(TestStatus.ERRORED, stepType + " errored: " + name);
				return;
			}
		}

		//for unhandled exceptions log on ui
		//exeLogger.error(ex, "An error occurred while executing " + stepType + ": " + name);
		ExecutorUtils.invokeErrorHandling(executable, new ErrorDetails(exeLogger, step, ex));
		setStatus(TestStatus.ERRORED, stepType + " errored: " + executable.name());
	}
}
