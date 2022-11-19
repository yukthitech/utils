package com.yukthitech.autox.debug.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ILocationBased;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.debug.common.DebugPoint;

/**
 * Execution debug manager to control flow execution.
 * @author akranthikiran
 */
public class DebugFlowManager
{
	private static DebugFlowManager instance = new DebugFlowManager();
	
	/**
	 * Currently available debug points.
	 */
	private Map<String, DebugPoint> debugPoints = new HashMap<>();
	
	/**
	 * Maintains list of live debug points (points where execution has stopped) along with callbacks.
	 */
	private Map<DebugPoint, Runnable> livePoints = new HashMap<>();
	
	private DebugFlowManager()
	{}
	
	public static DebugFlowManager getInstance()
	{
		return instance;
	}
	
	public synchronized DebugFlowManager addDebugPoints(Collection<DebugPoint> points)
	{
		if(CollectionUtils.isEmpty(points))
		{
			return this;
		}
		
		points.forEach(point -> debugPoints.put(point.getFilePath() + ":" + point.getLineNumber(), point));
		return this;
	}
	
	public synchronized DebugFlowManager removeDebugPoints(Collection<DebugPoint> points)
	{
		if(CollectionUtils.isEmpty(points))
		{
			return this;
		}
		
		points.forEach(point -> debugPoints.remove(point.getFilePath() + ":" + point.getLineNumber()));
		return this;
	}
	
	/**
	 * Checks if specified step has debug point, if it does throws debug exception, so that execution can be paused.
	 * 
	 * NOTE: Callback is supported for future use-cases, where multiple threads may participate in execution and only
	 * one thread is getting paused. 
	 * 
	 * @param executionName Name of the current execution (in future this would be current thread flow name)
	 * @param step step whose location should be checked for debug point 
	 * @param callback callback to be called once debugger chooses to execute and cross this step
	 */
	public synchronized void checkForDebugPoint(AutomationContext context, String executionName, ILocationBased step, Runnable callback)
	{
		String debugRef = step.getLocation().getPath() + ":" + step.getLineNumber();
		DebugPoint point = debugPoints.get(debugRef);
		
		if(point == null)
		{
			return;
		}
		
		//if the specified point was already live
		// and re-execution is started then point should be removed from live
		// and return
		
		if(livePoints.remove(point) != null)
		{
			return;
		}
		
		livePoints.put(point, callback);
		
		//inform execution is paused and once reexeucte, how to resume from that point
		context.getDebugServer().executionPaused(context, point);
		
		throw new DebugPointReachedException(step.getLocation(), step.getLineNumber());
	}
}
