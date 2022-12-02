package com.yukthitech.autox.debug.client;

import java.io.Serializable;

/**
 * Handler to handle async data received from server.
 * @author akiran
 */
public interface IDebugClientHandler
{
	/**
	 * Invoked when a data is received from server.
	 * @param data
	 */
	public void processData(Serializable data);
}
