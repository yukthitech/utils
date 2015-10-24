package com.yukthi.persistence.monitor;

import com.yukthi.persistence.EntityDetails;

/**
 * Listener to observe table creation of entity
 * @author akiran
 */
public interface IEntityCreateTableListener
{
	public void tableCreated(EntityDetails entityDetails);
}
