package com.yukthitech.persistence.repository.executors.proxy;

/**
 * Internal interface to be implemented by proxy entities.
 * @author akiran
 */
public interface IProxyEntity
{
	/**
	 * Fetches the id of the current entity.
	 * @return id of the current entity
	 */
	public Object $getProxyEntityId();
}
