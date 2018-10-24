package com.yukthitech.autox.monitor;

import java.io.Serializable;

/**
 * Handler to handle async data received from client.
 * @author akiran
 */
public interface IAsyncServerDataHandler
{
	/**
	 * Invoked when a data is received from client.
	 * @param data
	 */
	public void processData(Serializable data);
}
