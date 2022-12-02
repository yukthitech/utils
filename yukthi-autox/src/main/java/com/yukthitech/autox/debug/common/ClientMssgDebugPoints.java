package com.yukthitech.autox.debug.common;

import java.util.List;
import java.util.UUID;

/**
 * Used when debug points are added or removed.
 * @author akranthikiran
 */
public class ClientMssgDebugPoints extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Debug points to be used.
	 */
	private List<DebugPoint> debugPoints;
	
	public ClientMssgDebugPoints(List<DebugPoint> debugPoints)
	{
		super(UUID.randomUUID().toString());
		this.debugPoints = debugPoints;
	}

	public List<DebugPoint> getDebugPoints()
	{
		return debugPoints;
	}
}
