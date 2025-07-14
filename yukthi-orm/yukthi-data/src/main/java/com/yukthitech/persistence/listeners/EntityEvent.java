/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.persistence.listeners;

import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Event object representing listener event
 * @author akiran
 */
public class EntityEvent
{
	/**
	 * Entity under action
	 */
	private Object entity;
	
	/**
	 * Type of event
	 */
	private EntityEventType eventType;
	
	/**
	 * Repository factory
	 */
	private RepositoryFactory repositoryFactory;
	
	public EntityEvent(Object entity, EntityEventType eventType, RepositoryFactory factory)
	{
		this.entity = entity;
		this.eventType = eventType;
		this.repositoryFactory = factory;
	}

	/**
	 * @return the {@link #entity entity}
	 */
	public Object getEntity()
	{
		return entity;
	}

	/**
	 * @return the {@link #eventType eventType}
	 */
	public EntityEventType getEventType()
	{
		return eventType;
	}
	
	public RepositoryFactory getRepositoryFactory()
	{
		return repositoryFactory;
	}
}
