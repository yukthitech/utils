package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutoxValidationException;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.ErrorDetails;
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
	
	private ReportDataManager reportManager = ReportDataManager.getInstance();
	
	protected Object executable;
	
	protected Setup setup;
	
	protected Cleanup cleanup;
	
	protected Setup beforeChild;
	
	protected Cleanup afterChild;

	protected int parallelCount;

	private List<Executor> childExecutors;
	
	protected List<IStep> childSteps;
	
	private Executor parentExecutor;
	
	protected TestStatus status;
	
	protected String statusMessage;
	
	protected ExpectedException expectedException;
	
	protected Executor(Object executable)
	{
		this.executable = executable;
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
	
	/**
	 * This is invoked once all dependencies (if any) are completed and this executor is ready to execute.
	 * In this method data-provider execution (and child population) should occur.
	 */
	public boolean init()
	{
		return true;
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
	
	public void execute(Setup beforeChildFromParent, Cleanup afterChildFromParent)
	{
		status = TestStatus.IN_PROGRESS;

		AutomationContext.getInstance().getExecutionStack().push(executable);
		reportManager.executionStarted(ExecutionType.MAIN, this);
		
		try
		{
			if(!ExecutorUtils.executeSetup(beforeChildFromParent, "Before-Child", this))
			{
				return;
			}
					
			//Execute setup
			if(!ExecutorUtils.executeSetup(setup, "Setup", this))
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
	
				ExecutorUtils.executeCleanup(afterChildFromParent, "After-Child", this);
			}
		} finally
		{
			closeReportManager();
			AutomationContext.getInstance().getExecutionStack().pop(executable);
		}
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

	private void executeChildExecutors()
	{
		ExecutionPool.getInstance().execute(childExecutors, beforeChild, afterChild, parallelCount);
		
		TestStatus finalStatus = TestStatus.SUCCESSFUL;
		
		for(Executor cexecutor : childExecutors)
		{
			if(cexecutor.status == TestStatus.ERRORED)
			{
				finalStatus = TestStatus.ERRORED;
			}
			else if(cexecutor.status == TestStatus.FAILED && finalStatus != TestStatus.ERRORED)
			{
				finalStatus = TestStatus.FAILED;
			}
			else if(cexecutor.status == TestStatus.SKIPPED && !finalStatus.isErrored())
			{
				finalStatus = TestStatus.SKIPPED;
			}
		}
		
		if(finalStatus == TestStatus.ERRORED)
		{
			setStatus(TestStatus.ERRORED, "Child execution(s) errored");
		}
		else if(finalStatus == TestStatus.FAILED)
		{
			setStatus(TestStatus.FAILED, "Child execution(s) failed");
		}
		if(finalStatus == TestStatus.SKIPPED)
		{
			setStatus(TestStatus.SKIPPED, "Child execution(s) skipped");
		}
		else
		{
			setStatus(TestStatus.SUCCESSFUL, null);
		}
	}
	
	private void executeChildSteps()
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
