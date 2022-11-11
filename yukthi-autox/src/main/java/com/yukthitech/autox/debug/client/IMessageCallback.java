package com.yukthitech.autox.debug.client;

import com.yukthitech.autox.debug.common.MessageConfirmationServerMssg;

/**
 * Callback interface to be used to get callback in message processing.
 * @author akiran
 */
public interface IMessageCallback
{
	/**
	 * Invoked when target message is processed.
	 * @param confirmation confirmation mssg received from server.
	 */
	public void onProcess(MessageConfirmationServerMssg confirmation);
	
	/**
	 * Invoked when child process gets terminated abruptly.
	 */
	public default void terminated()
	{}
}
