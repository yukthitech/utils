package com.yukthitech.persistence.repository.executors.proxy;

import java.lang.reflect.Method;
import java.util.Collection;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IInternalRepository;
import com.yukthitech.persistence.repository.executors.FetchJoinEntityQueryExecutor;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

/**
 * Proxy used to fetch sub entities which are joined by join table.
 * @author akiran
 */
public class ProxyJoinEntityCollection
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
	 * Proxy object that will be exposed to outside world
	 */
	private Object proxyCollection;
	
	/**
	 * Query executor to be used to fetch join entity collection.
	 */
	private FetchJoinEntityQueryExecutor fetchJoinEntityQueryExecutor;
	
	/**
	 * Parent id based on which entities needs to be fetched.
	 */
	private Object parentId;
	
	@SuppressWarnings("unchecked")
	public static Collection<Object> newProxyCollection(EntityDetails entityDetails, ICrudRepository<?> repository, String fieldName, Object parentId)
	{
		ProxyJoinEntityCollection proxyEntityCollection = new ProxyJoinEntityCollection(entityDetails, repository, fieldName, parentId);
		return (Collection<Object>) proxyEntityCollection.proxyCollection;
	}
	
	/**
	 * Instantiates a new proxy join entity collection.
	 *
	 * @param entityDetails the entity details
	 * @param repository the repository
	 * @param fieldName the field name
	 * @param parentId the parent id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ProxyJoinEntityCollection(EntityDetails entityDetails, ICrudRepository<?> repository, String fieldName, Object parentId)
	{
		this.repository = repository;
		this.fetchJoinEntityQueryExecutor = new FetchJoinEntityQueryExecutor(entityDetails, fieldName);
		this.parentId = parentId;
		
		Class<?> collectionType = fetchJoinEntityQueryExecutor.getCollectionReturnType();
		
		//create ccg lib handler which will handle method calls on proxy
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(collectionType);
		
		try
		{
			this.actualCollection = (Collection) collectionType.newInstance();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating collection of type: " + collectionType.getName(), ex);
		}
		
		enhancer.setCallback(new InvocationHandler()
		{
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				return ProxyJoinEntityCollection.this.invoke(proxy, method, args);
			}
		});
		
		this.proxyCollection = enhancer.create();
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
		Collection<Object> entities = (Collection) ((IInternalRepository) repository).executeQueryExecutor(fetchJoinEntityQueryExecutor, parentId);
		
		if(entities != null)
		{
			this.actualCollection.addAll(entities);
		}
		
		collectionLoaded = true;
		return method.invoke(actualCollection, args);
	}
	
}
