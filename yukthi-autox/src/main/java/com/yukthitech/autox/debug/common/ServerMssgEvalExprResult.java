package com.yukthitech.autox.debug.common;

/**
 * Response message for eval expression along with result.
 * @author akranthikiran
 */
public class ServerMssgEvalExprResult extends ServerMssgConfirmation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Result value.
	 */
	private Object result;
	
	public ServerMssgEvalExprResult(String requestId, boolean successful, Object result, String errorMssg, Object... mssgArgs)
	{
		super(requestId, successful, errorMssg, mssgArgs);
		this.result = result;
	}

	public Object getResult()
	{
		return result;
	}
}
