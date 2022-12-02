package com.yukthitech.autox.debug.common;

import java.io.Serializable;

/**
 * Used to send success/failure confirmation for client request.
 * @author akranthikiran
 */
public class ServerMssgConfirmation implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String requestId;
	
	/**
	 * Flag indicating corresponding request processing is successful or not.
	 */
	private boolean successful;
	
	/**
	 * Error details in case request processing failed.
	 */
	private String error;

	public ServerMssgConfirmation(String requestId, boolean successful, String errorMssg, Object... mssgArgs)
	{
		this.requestId = requestId;
		this.successful = successful;
		
		if(errorMssg != null)
		{
			this.error = String.format(errorMssg, mssgArgs);
		}
	}

	public String getRequestId()
	{
		return requestId;
	}

	public boolean isSuccessful()
	{
		return successful;
	}

	public String getError()
	{
		return error;
	}
}
