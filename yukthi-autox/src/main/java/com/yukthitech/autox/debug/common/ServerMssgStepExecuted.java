package com.yukthitech.autox.debug.common;

import java.util.Map;

/**
 * Response message sent by server post successful execution of steps.
 * @author akranthikiran
 */
public class ServerMssgStepExecuted extends ServerMssgConfirmation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Step execution request id.
	 */
	private Map<String, byte[]> contextAttr;
	
	public ServerMssgStepExecuted(String requestId, boolean successful, Map<String, byte[]> contextAttr, String errorMssg, Object... mssgArgs)
	{
		super(requestId, successful, errorMssg, mssgArgs);
		this.contextAttr = contextAttr;
	}
	public Map<String, byte[]> getContextAttr()
	{
		return contextAttr;
	}
}
