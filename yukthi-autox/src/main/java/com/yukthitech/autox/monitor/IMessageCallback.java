package com.yukthitech.autox.monitor;

import com.yukthitech.autox.monitor.ienv.MessageConfirmation;

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
	public void onProcess(MessageConfirmation confirmation);
	
	/**
	 * Invoked when child process gets terminated abruptly.
	 */
	public default void terminated()
	{}
}
