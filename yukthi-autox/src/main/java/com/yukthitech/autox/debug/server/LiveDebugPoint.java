package com.yukthitech.autox.debug.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ILocationBased;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.debug.common.DebugOp;
import com.yukthitech.autox.debug.common.DebugPoint;
import com.yukthitech.autox.debug.common.ServerMssgConfirmation;
import com.yukthitech.autox.debug.common.ServerMssgEvalExprResult;
import com.yukthitech.autox.debug.common.ServerMssgExecutionPaused;
import com.yukthitech.autox.debug.common.ServerMssgExecutionReleased;
import com.yukthitech.autox.debug.common.ServerMssgStepExecuted;
import com.yukthitech.autox.exec.StepsExecutor;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a live debug point where a thread is on hold.
 * @author akranthikiran
 */
public class LiveDebugPoint
{
	private static Logger logger = LogManager.getLogger(LiveDebugPoint.class);
	private static ThreadLocal<LiveDebugPoint> livePointThreadLocal = new ThreadLocal<>();
	
	private static byte[] NOT_SER_BYTES = "<Not Serializable>".getBytes();
	
	private static class Request
	{
		private String requestId;
		
		private Object data;

		public Request(String requestId, Object data)
		{
			this.requestId = requestId;
			this.data = data;
		}
	}
	
	private String id = UUID.randomUUID().toString();
	
	private DebugPoint debugPoint;
	
	private Thread threadOnHold;
	
	private boolean released = false;
	
	private LinkedList<Request> requests = new LinkedList<>();
	
	/**
	 * Last location where executed was halt is maintained here.
	 */
	private ILocationBased lastPauseLocation;
	
	private DebugOp lastDebugOp;
	
	private AtomicBoolean onPause = new AtomicBoolean(false);
	
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition releaseRequestCondition = pauseLock.newCondition();
	
	private LiveDebugPoint(ILocationBased location, DebugPoint debugPoint, Consumer<LiveDebugPoint> callback)
	{
		this.debugPoint = debugPoint;
		this.threadOnHold = Thread.currentThread();
		
		pause(location, callback);
	}
	
	public static LiveDebugPoint pauseAtDebugPoint(ILocationBased location, DebugPoint debugPoint, Consumer<LiveDebugPoint> callback)
	{
		LiveDebugPoint liveDebugPoint = new LiveDebugPoint(location, debugPoint, callback);
		return liveDebugPoint;
	}
	
	public static LiveDebugPoint getLivePoint()
	{
		LiveDebugPoint point = livePointThreadLocal.get();
		
		if(point != null && point.released)
		{
			livePointThreadLocal.remove();
			return null;
		}
		
		return point;
	}

	public String getId()
	{
		return id;
	}
	
	public String getName()
	{
		return threadOnHold.getName();
	}
	
	public DebugPoint getDebugPoint()
	{
		return debugPoint;
	}
	
	private Map<String, byte[]> getContextAttr()
	{
		Map<String, byte[]> contextAttr = new HashMap<>(); 
		ExecutionContextManager.getExecutionContext().getAttr().forEach((key, val) ->
		{
			byte serData[] = null;
			
			try
			{
				serData = SerializationUtils.serialize((Serializable) val);
			}catch(Exception ex)
			{
				serData = NOT_SER_BYTES;
			}
			
			contextAttr.put(key, serData);
		});

		return contextAttr;
	}
	
	private void sendOnHoldMssg()
	{
		List<ServerMssgExecutionPaused.StackElement> stackTrace = ExecutionContextManager
				.getInstance().getExecutionStack()
				.getStackTrace()
				.stream()
				.map(se -> new ServerMssgExecutionPaused.StackElement(se.getLocationName(), se.getLineNumber()))
				.collect(Collectors.toList());
		
		stackTrace = new ArrayList<>(stackTrace);
		
		ServerMssgExecutionPaused pausedMssg = new ServerMssgExecutionPaused(id, lastPauseLocation.getLocation().getPath(), 
				lastPauseLocation.getLineNumber(), stackTrace, getContextAttr());
		DebugServer.getInstance().sendClientMessage(pausedMssg);
	}
	
	private void pause(ILocationBased location, Consumer<LiveDebugPoint> callback)
	{
		livePointThreadLocal.set(this);
		
		pauseLock.lock();
		
		try
		{
			if(callback != null)
			{
				callback.accept(this);
			}
			
			this.lastPauseLocation = location;
			
			sendOnHoldMssg();
			
			logger.trace("LivePOINT: Pause at location: {}:{}", lastPauseLocation.getLocation().getName(), lastPauseLocation.getLineNumber());
			
			onPause.set(true);

			while(!released)
			{
				try
				{
					//wait for release request
					releaseRequestCondition.await();
					break;
				}catch(InterruptedException ex)
				{
					handleOnHoldTasks();
				}
			}
			
			logger.trace("LivePOINT: Pause released at location: {}:{}", lastPauseLocation.getLocation().getName(), lastPauseLocation.getLineNumber());
			
			DebugServer.getInstance().sendClientMessage(new ServerMssgExecutionReleased(id));
		}catch(Exception ex)
		{
			logger.error("An error occurred during debug point hold", ex);
		} finally
		{
			onPause.set(false);
			pauseLock.unlock();
		}
	}
	
	private void executeStepsRequest(String reqId, List<IStep> steps)
	{
		try
		{
			IExecutionLogger logger = ExecutionContextManager.getExecutionContext().getExecutionLogger();
			StepsExecutor.execute(logger, steps, null);
			
			DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, true, getContextAttr(), null));
		}catch(Exception ex)
		{
			logger.error("An error occurred during dynamic step execution", ex);
			DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, false, getContextAttr(), 
					"An error occurred during dynamic step execution:\n  " + ex));
		}
	}

	private void evalExprRequest(String reqId, String expression)
	{
		try
		{
			//if no prefix is used, by default use expr prefix
			if(!ExpressionFactory.isExpression(expression))
			{
				expression = "expr: " + expression;
			}
			
			Object res = ExpressionFactory.getExpressionFactory().parseExpression(AutomationContext.getInstance(), expression);
			DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(reqId, true, res, null));
		}catch(Exception ex)
		{
			logger.error("An error occurred during expression evaluation: {}", expression, ex);
			DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, false, null, 
					"An error occurred during expression evaluation:\n  " + ex));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void handleOnHoldTasks()
	{
		List<Request> currentRequests = new ArrayList<>();
		
		synchronized(requests)
		{
			if(requests.isEmpty())
			{
				return;
			}

			currentRequests.addAll(this.requests);
			this.requests.clear();
		}

		for(Request req : currentRequests)
		{
			if(req.data instanceof List)
			{
				executeStepsRequest(req.requestId, (List) req.data);
			}
			else
			{
				evalExprRequest(req.requestId, (String) req.data);
			}
		}
	}
	
	public void executeSteps(String reqId, List<IStep> steps)
	{
		pauseLock.lock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgStepExecuted(reqId, false, null, "Current live-point is not in paused state"));
				return;
			}
			
			this.requests.addLast(new Request(reqId, steps));
			threadOnHold.interrupt();
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public void evalExpression(String reqId, String expression)
	{
		pauseLock.lock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgEvalExprResult(reqId, false, null, "Current live-point is not in paused state"));
				return;
			}
			
			synchronized(requests)
			{
				this.requests.addLast(new Request(reqId, expression));
			}
			
			threadOnHold.interrupt();
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public boolean isDynamicExecutionInProgress()
	{
		pauseLock.lock();
		
		try
		{
			return onPause.get() && !requests.isEmpty();
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public boolean release(String reqId, DebugOp debugOp)
	{
		logger.trace("LivePOINT: Release operation request with op: {}", debugOp);
		
		pauseLock.lock();
		
		try
		{
			if(!onPause.get())
			{
				DebugServer.getInstance().sendClientMessage(new ServerMssgConfirmation(reqId, false, "Current live-point is not in paused state"));
				return false;
			}
			
			if(debugOp == DebugOp.STEP_RETURN)
			{
				clearThread();
			}
			else
			{
				this.lastDebugOp = debugOp;
				releaseRequestCondition.signal();
			}
			
			return true;
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	public void checkForPause(ILocationBased step)
	{
		if(threadOnHold != Thread.currentThread())
		{
			throw new InvalidStateException("Pause check is called on non-owner thread");
		}
		
		logger.trace("LivePOINT: Checking for pause at location: {}", step);
		
		pauseLock.lock();
		
		try
		{
			DebugOp lastDebugOp = this.lastDebugOp;
			
			//when stepping into function, hold irrespective of location
			if(lastDebugOp == DebugOp.STEP_INTO)
			{
				//clear last debug op, as step-into will be completed
				//  by this pause
				this.lastDebugOp = null;
				pause(step, null);
				return;
			}
			//when moving to next step, dont stop in case child steps are getting executed
			//  like steps in function call invoked from last location
			else if(lastDebugOp == DebugOp.STEP_OVER)
			{
				boolean subExecution = ExecutionContextManager.getInstance().getExecutionStack().isSubexecutionOf(lastPauseLocation);
				
				if(!subExecution)
				{
					//clear last debug op, as step-over will be completed
					//  by this pause
					this.lastDebugOp = null;
					pause(step, null);
				}
				
				return;
			}
			
			//Note: In case of step-return this live-point should have been released
			
			throw new InvalidStateException("Check for hold is called during debug operation: {}", lastDebugOp);
		}finally
		{
			pauseLock.unlock();
		}
	}
	
	private void releaseLivePoint()
	{
		//remove from thread local as well (in case the execution directly happen)
		// post debug point halt
		
		//if current thread is same as owner, clear thread local
		// Note: though not removed from thread local, once marked as release getLivePoint() will not 
		//  return current live point (and also clears it)
		if(this.threadOnHold == Thread.currentThread())
		{
			livePointThreadLocal.remove();
		}
		
		released = true;
		DebugFlowManager.getInstance().removeLivePoint(id);
	}
	
	public void clearThread()
	{
		pauseLock.lock();
		
		try
		{
			if(released)
			{
				return;
			}
			
			releaseLivePoint();

			//if thread is not released, release it
			releaseRequestCondition.signal();
		}finally
		{
			pauseLock.unlock();
		}
	}
}