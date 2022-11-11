package com.yukthitech.autox.debug.server.handler;

import java.io.Serializable;

import com.yukthitech.autox.debug.server.IServerDataHandler;

public abstract class AbstractServerDataHandler<D extends Serializable> implements IServerDataHandler<D>
{
	private Class<D> dataType;

	public AbstractServerDataHandler(Class<D> dataType)
	{
		this.dataType = dataType;
	}
	
	@Override
	public Class<D> getSupportedDataType()
	{
		return dataType;
	}
}
