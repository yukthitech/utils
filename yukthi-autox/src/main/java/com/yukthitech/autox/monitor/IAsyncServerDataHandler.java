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
	 * @return true if data is processed.
	 */
	public boolean processData(Serializable data);
}
