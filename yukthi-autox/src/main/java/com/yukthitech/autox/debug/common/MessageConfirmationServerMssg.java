package com.yukthitech.autox.debug.common;

import java.io.Serializable;

/**
 * Generic response object used to send confirmation with the processing details.
 * @author akiran
 */
public class MessageConfirmationServerMssg implements Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id of the request object for which confirmation is being sent.
	 */
	private String requestId;
	
	/**
	 * Flag indicating if the message is processed successfully or not.
	 */
	private boolean successful;
	
	/**
	 * Error message in case message processing resulted in error.
	 */
	private String errorMessage;

	/**
	 * Instantiates a new message confirmation.
	 *
	 * @param requestId the request id
	 * @param successful the successful
	 * @param errorMessage the error message
	 */
	public MessageConfirmationServerMssg(String requestId, boolean successful, String errorMessage)
	{
		this.requestId = requestId;
		this.successful = successful;
		this.errorMessage = errorMessage;
	}

	/**
	 * Gets the id of the request object for which confirmation is being sent.
	 *
	 * @return the id of the request object for which confirmation is being sent
	 */
	public String getRequestId()
	{
		return requestId;
	}

	/**
	 * Gets the flag indicating if the message is processed successfully or not.
	 *
	 * @return the flag indicating if the message is processed successfully or not
	 */
	public boolean isSuccessful()
	{
		return successful;
	}

	/**
	 * Gets the error message in case message processing resulted in error.
	 *
	 * @return the error message in case message processing resulted in error
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}
}
