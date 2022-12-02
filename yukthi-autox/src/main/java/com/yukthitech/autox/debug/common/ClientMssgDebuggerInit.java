package com.yukthitech.autox.debug.common;

import java.util.List;
import java.util.UUID;

/**
 * Initial data expected to be sent by client to server.
 * @author akranthikiran
 */
public class ClientMssgDebuggerInit extends ClientMessage
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Debug points to be used.
	 */
	private List<DebugPoint> debugPoints;
	
	public ClientMssgDebuggerInit(List<DebugPoint> debugPoints)
	{
		super(UUID.randomUUID().toString());
		this.debugPoints = debugPoints;
	}

	public List<DebugPoint> getDebugPoints()
	{
		return debugPoints;
	}
}
