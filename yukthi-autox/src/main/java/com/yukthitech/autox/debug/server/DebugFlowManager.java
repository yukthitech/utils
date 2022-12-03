package com.yukthitech.autox.debug.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ILocationBased;
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
	private Map<String, LiveDebugPoint> livePoints = new HashMap<>();
	
	private DebugFlowManager()
	{}
	
	public static DebugFlowManager getInstance()
	{
		return instance;
	}
	
	public void reset()
	{
		synchronized(livePoints)
		{
			List<LiveDebugPoint> points = new ArrayList<>(this.livePoints.values());
			
			points.forEach(livePoint -> 
			{
				livePoint.clearThread();
			});
			
			this.livePoints.clear();
		}

		synchronized(debugPoints)
		{
			this.debugPoints.clear();
		}
	}
	
	public DebugFlowManager setDebugPoints(Collection<DebugPoint> points)
	{
		if(CollectionUtils.isEmpty(points))
		{
			return this;
		}
		
		synchronized(debugPoints)
		{
			this.debugPoints.clear();
			points.forEach(point -> debugPoints.put(point.getFilePath() + ":" + point.getLineNumber(), point));
		}
		
		return this;
	}
	
	/**
	 * Checks if specified step has debug point, if it does throws debug exception, so that execution can be paused.
	 * 
	 * NOTE: Callback is supported for future use-cases, where multiple threads may participate in execution and only
	 * one thread is getting paused. 
	 * 
	 * @param step step whose location should be checked for debug point 
	 */
	public void checkForDebugPoint(ILocationBased step)
	{
		if(!DebugServer.isRunningInDebugMode())
		{
			return;
		}
		
		LiveDebugPoint currentLivePoint = LiveDebugPoint.getLivePoint();
		
		if(currentLivePoint != null)
		{
			//when dynamic steps under execution dont look for debug points
			if(currentLivePoint.isDynamicExecutionInProgress())
			{
				return;
			}
			
			//if live point is able to handle the flow, return 
			if(currentLivePoint.checkForPause(step))
			{
				return;
			}
		}

		//if live point is not present or unable to determine, check for other debug points
		String debugRef = step.getLocation().getPath() + ":" + step.getLineNumber();
		DebugPoint point = null;
		
		synchronized(debugPoints)
		{
			point = debugPoints.get(debugRef);
		}
		
		if(point == null)
		{
			return;
		}
		
		//if live point is present, then before pausing at other debug point, release current live point
		if(currentLivePoint != null)
		{
			currentLivePoint.clearThread();
		}
		
		LiveDebugPoint.pauseAtDebugPoint(step, point, livePoint -> 
		{
			synchronized(livePoints)
			{
				this.livePoints.put(livePoint.getId(), livePoint);	
			}
		});
	}
	
	public LiveDebugPoint getLiveDebugPoint(String id)
	{
		synchronized(livePoints)
		{
			return livePoints.get(id);
		}
	}
	
	public void removeLivePoint(String id)
	{
		synchronized(livePoints)
		{
			livePoints.remove(id);
		}
	}
}
