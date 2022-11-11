package com.yukthitech.autox.debug.server;

import java.io.Serializable;

/**
 * Handler to handle async data received from client.
 * @author akiran
 */
public interface IServerDataHandler<D extends Serializable>
{
	/**
	 * Should return the type of data this handler can handle.
	 * @return
	 */
	public Class<D> getSupportedDataType();
	
	/**
	 * Invoked when a data is received from client.
	 * @param data
	 * @return true if data is processed.
	 */
	public boolean processData(D data);
}
