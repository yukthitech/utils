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
package com.yukthitech.persistence.repository.executors.proxy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.persistence.utils.OrmUtils;

/**
 * Represents proxy for entity class used for lazy loading
 * @author akiran
 */
public class ProxyEntityCollection
{
	/**
	 * The actual entity collection which would be loaded lazily on need basis 
	 */
	private Collection<Object> actualCollection;
	
	/**
	 * Flag indicating if target entities are loaded into collection
	 */
	private boolean collectionLoaded;
	
	/**
	 * CRUD repository for the entity
	 */
	private ICrudRepository<?> repository;
	
	/**
	 * Condition to fetch collection
	 */
	private SearchCondition searchCondition;
	
	/**
	 * Proxy object that will be exposed to outside world
	 */
	private Object proxyCollection;
	
	@SuppressWarnings("unchecked")
	public static Collection<Object> newProxyCollectionByCondition(EntityDetails entityDetails, ICrudRepository<?> repository, SearchCondition condition, Class<?> collectionType)
	{
		ProxyEntityCollection proxyEntityCollection = new ProxyEntityCollection(entityDetails, repository, condition, collectionType);
		return (Collection<Object>) proxyEntityCollection.proxyCollection;
	}
	
	/**
	 * Creates a proxy for specified entity type
	 * @param entityDetails
	 * @param repository
	 * @param condition
	 * @param actualCollection
	 */
	private ProxyEntityCollection(EntityDetails entityDetails, ICrudRepository<?> repository, SearchCondition condition, Class<?> collectionType)
	{
		if(condition == null)
		{
			throw new NullPointerException("Search-condition can not be null");
		}
		
		this.repository = repository;
		this.searchCondition = condition;
		
		this.actualCollection = OrmUtils.createCollection(collectionType);
		
		//create ccg lib handler which will handle method calls on proxy
		this.proxyCollection = ProxyBuilder.buildProxy(collectionType, null, this::invoke);
	}
	
	/**
	 * Proxy method invocation handler method
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	private synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if(collectionLoaded)
		{
			return method.invoke(actualCollection, args);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Object> entities = (List)repository.search(new SearchQuery(this.searchCondition));
		
		if(entities != null)
		{
			this.actualCollection.addAll(entities);
		}
		
		collectionLoaded = true;
		return method.invoke(actualCollection, args);
	}
	
}
