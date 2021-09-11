package com.yukthitech.persistence.repository;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Table;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.EntityDetailsFactory;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.IInternalRepository;
import com.yukthitech.persistence.InvalidMappingException;
import com.yukthitech.persistence.listeners.EntityListenerManager;

public class RepositoryFactory
{
	/**
	 * Name of the factory used for easy identificaion.
	 */
	private String name;
	
	private IDataStore dataStore;
	
	private Map<Class<?>, ICrudRepository<?>> typeToRepo = new HashMap<>();
	private Map<Class<?>, ICrudRepository<?>> entityTypeToRepo = new HashMap<>();

	private boolean createTables;
	
	private ExecutorFactory executorFactory;
	
	private EntityDetailsFactory entityDetailsFactory = new EntityDetailsFactory();
	
	/**
	 * Manager to manage listeners and handling events
	 */
	private EntityListenerManager listenerManager = new EntityListenerManager();
	
	public IDataStore getDataStore()
	{
		return dataStore;
	}

	public void setDataStore(IDataStore dataStore)
	{
		this.dataStore = dataStore;
		dataStore.setEntityDetailsFactory(entityDetailsFactory);
	}
	
	public boolean isCreateTables()
	{
		return createTables;
	}

	public void setCreateTables(boolean createTables)
	{
		this.createTables = createTables;
	}
	
	/**
	 * Registers specified listener container 
	 * @param listenerContainer
	 */
	public void registerListeners(Object listenerContainer)
	{
		listenerManager.registerListener(listenerContainer);
	}
	
	public EntityListenerManager getEntityListenerManager()
	{
		return listenerManager;
	}
	
	public ExecutorFactory getExecutorFactory()
	{
		if(executorFactory == null)
		{
			executorFactory = new ExecutorFactory(new PersistenceExecutionContext(this));
		}
		
		return executorFactory;
	}

	public void setExecutorFactory(ExecutorFactory executorFactory)
	{
		this.executorFactory = executorFactory;
	}
	
	@SuppressWarnings({"rawtypes"})
	private EntityDetails fetchEntityDetails(Class<?> repositoryType)
	{
		if(!ICrudRepository.class.isAssignableFrom(repositoryType))
		{
			throw new IllegalStateException("Specified type does not extend ICrudRepository: " + repositoryType.getName());
		}
		
		Map<TypeVariable<?>, Type> typeMap = TypeUtils.getTypeArguments(repositoryType, ICrudRepository.class);
		
		Class<?> entityType = (Class<?>)typeMap.get(ICrudRepository.class.getTypeParameters()[0]);

		Table table = entityType.getAnnotation(Table.class);
		
		if(table == null)
		{
			throw new InvalidMappingException("No @Table annotation found on entity type: " + entityType.getName());
		}
		
		if(!createTables && !dataStore.tableExists(table.name()))
		{
			throw new NoTableExistsException(repositoryType, table.name());
		}
		
		return entityDetailsFactory.getEntityDetails((Class)entityType, dataStore, createTables);

		/*
		Type crudRepoType = fetchRepositoryType(repositoryType);
		
		if(crudRepoType == null)
		{
			
		}
		
		Type crudRepoParams[] = ((ParameterizedType)crudRepoType).getActualTypeArguments();
		Class<?> entityType = (Class<?>)crudRepoParams[0];
		
		Table table = entityType.getAnnotation(Table.class);
		
		if(table == null)
		{
			throw new InvalidMappingException("No @Table annotation found on entity type: " + entityType.getName());
		}
		
		return entityDetailsFactory.getEntityDetails((Class)entityType, dataStore, createTables);
		*/
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <R extends ICrudRepository<?>> R getRepository(Class<R> repositoryType)
	{
		R repo = (R)typeToRepo.get(repositoryType);
		
		if(repo != null)
		{
			return repo;
		}
		
		EntityDetails entityDetails = fetchEntityDetails(repositoryType);
		RepositoryProxy proxyImpl = new RepositoryProxy(dataStore, repositoryType, entityDetails, getExecutorFactory(), this);
		
		repo = (R)Proxy.newProxyInstance(RepositoryFactory.class.getClassLoader(), new Class<?>[] {repositoryType, IInternalRepository.class}, proxyImpl);
		typeToRepo.put(repositoryType, repo);
		entityTypeToRepo.put(entityDetails.getEntityType(), repo);
		
		return repo;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private synchronized ICrudRepository<?> getGenericRepository(Class<?> entityType)
	{
		ICrudRepository<?> repo = entityTypeToRepo.get(entityType);
		
		if(repo != null)
		{
			return repo;
		}

		EntityDetails entityDetails = entityDetailsFactory.getEntityDetails((Class)entityType, dataStore, createTables);
		RepositoryProxy proxyImpl = new RepositoryProxy(dataStore, (Class)ICrudRepository.class, entityDetails, getExecutorFactory(), this);
		
		repo = (ICrudRepository)Proxy.newProxyInstance(RepositoryFactory.class.getClassLoader(), 
							new Class<?>[] {ICrudRepository.class, IInternalRepository.class}, proxyImpl);
		entityTypeToRepo.put(entityType, repo);
		
		return repo;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> ICrudRepository<T> getRepositoryForEntity(Class<T> entityType)
	{
		ICrudRepository<?> repo = entityTypeToRepo.get(entityType);
		
		if(repo != null)
		{
			return (ICrudRepository)repo;
		}
		
		return (ICrudRepository)getGenericRepository(entityType);
	}
	
	/**
	 * Drops the specified entity type table and cleans up local caches
	 * @param entityType
	 */
	public <T> void dropRepository(Class<T> entityType)
	{
		IInternalRepository repository = (IInternalRepository)getRepositoryForEntity(entityType);

		//drop the underlying data store table
		repository.dropEntityTable();
		
		//remove from entity details factory, so that required tables will get auto created
		entityDetailsFactory.removeEntityDetails(entityType);
	
		//remove from local entity type cache
		this.entityTypeToRepo.remove(entityType);
		
		//remove from local repository type cache
		Class<?> actualRepoType = repository.getRepositoryType();
		
		//if target repository type is not generic type
		if(!ICrudRepository.class.equals(actualRepoType))
		{
			this.typeToRepo.remove(actualRepoType);
		}
	}

	/**
	 * Gets the name of the factory used for easy identificaion.
	 *
	 * @return the name of the factory used for easy identificaion
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the factory used for easy identificaion.
	 *
	 * @param name the new name of the factory used for easy identificaion
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the flag indicating if unique id column should be added during table creation. This in turn will be used to fetch auto generated id on need basis.
	 *
	 * @return the flag indicating if unique id column should be added during table creation
	 */
	public boolean isAddUniqueIdColumnEnabled()
	{
		return entityDetailsFactory.isAddUniqueIdColumnEnabled();
	}

	/**
	 * Sets the flag indicating if unique id column should be added during table creation. This in turn will be used to fetch auto generated id on need basis.
	 *
	 * @param addUniqueIdColumnEnabled the new flag indicating if unique id column should be added during table creation
	 */
	public void setAddUniqueIdColumnEnabled(boolean addUniqueIdColumnEnabled)
	{
		entityDetailsFactory.setAddUniqueIdColumnEnabled(addUniqueIdColumnEnabled);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);

		builder.append("]");
		return builder.toString();
	}

}
