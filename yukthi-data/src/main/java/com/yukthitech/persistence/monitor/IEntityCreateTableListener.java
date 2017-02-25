package com.yukthitech.persistence.monitor;

import com.yukthitech.persistence.EntityDetails;

/**
 * Listener to observe table creation of entity
 * @author akiran
 */
public interface IEntityCreateTableListener
{
	public void tableCreated(EntityDetails entityDetails);
}
