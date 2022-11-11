package com.yukthitech.autox.debug.common;

import java.io.Serializable;
import java.util.List;

/**
 * Initial data expected to be sent by client to server.
 * @author akranthikiran
 */
public class DebuggerInitClientMssg implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Debug points to be used.
	 */
	private List<DebugPoint> debugPoints;

	public List<DebugPoint> getDebugPoints()
	{
		return debugPoints;
	}

	public void setDebugPoints(List<DebugPoint> debugPoints)
	{
		this.debugPoints = debugPoints;
	}
}
