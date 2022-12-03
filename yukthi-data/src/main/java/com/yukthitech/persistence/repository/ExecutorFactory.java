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
package com.yukthitech.persistence.repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.repository.executors.AggregateQueryExecutor;
import com.yukthitech.persistence.repository.executors.DeleteQueryExecutor;
import com.yukthitech.persistence.repository.executors.FinderQueryExecutor;
import com.yukthitech.persistence.repository.executors.NativeQueryExecutor;
import com.yukthitech.persistence.repository.executors.QueryExecutor;
import com.yukthitech.persistence.repository.executors.QueryExecutorPattern;
import com.yukthitech.persistence.repository.executors.SaveQueryExecutor;
import com.yukthitech.persistence.repository.executors.SearchQueryExecutor;
import com.yukthitech.persistence.repository.executors.UpdateQueryExecutor;
import com.yukthitech.utils.annotations.RecursiveAnnotationFactory;

/**
 * Factory to get query executor for specified repository method. A query executor for a repository method can be determined using
 * 		1) Annotations on repository method
 * 		2) Method name prefixes
 * 
 * This configuration (whether and what annotation to use or prefixes to be used or excluded) is defined using annotaion {@link QueryExecutorPattern} on the
 * executor class being registered.
 * @author akiran
 */
public class ExecutorFactory
{
	/**
	 * Executor details like which constructor to use for executor creation. And which method prefixes or suffixes to be used to match
	 * repository method.
	 * @author akiran
	 */
	private static class ExecutorDetails
	{
		/**
		 * Constructor for executor creation
		 */
		private Constructor<?> constructor;
		
		/**
		 * Prefixes to be used for repository method matching
		 */
		private String prefixes[];
		
		/**
		 * Prefixes to be excluded during repository method match
		 */
		private String excludePrefixes[];
		
		public ExecutorDetails(Constructor<?> constructor, String prefixes[], String excludePrefixes[])
		{
			this.constructor = constructor;
			this.prefixes = prefixes;
			this.excludePrefixes = excludePrefixes;
		}
		
		/**
		 * Creates new query executor with specified details
		 * @param persistenceExecutionContext
		 * @param repositoryType Repository type
		 * @param method matched repository method
		 * @param entityDetails Target entity details
		 * @return
		 */
		public QueryExecutor newQueryExecutor(PersistenceExecutionContext persistenceExecutionContext, Class<?> repositoryType, Method method, EntityDetails entityDetails)
		{
			try
			{
				QueryExecutor execuctor = (QueryExecutor)constructor.newInstance(repositoryType, method, entityDetails);
				execuctor.setPersistenceExecutionContext(persistenceExecutionContext);
				
				return execuctor;
			}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
			{
				throw new IllegalStateException("An error occurred while creating instance of Query-executor", ex);
			}
		}
		
		/**
		 * Checks if the specified method name matches with the current executor prefix conditions
		 * @param methodName
		 * @return
		 */
		public boolean isMatchingMethodName(String methodName)
		{
			//if no prefixes are specified return mismatch
			if(this.prefixes == null)
			{
				return false;
			}
			
			//check if method name has excluded prefixes
			if(this.excludePrefixes != null)
			{
				for(String exPrefix: this.excludePrefixes)
				{
					//if name matches with any of excluded prefix
					if(methodName.startsWith(exPrefix))
					{
						//return mismatch
						return false;
					}
				}
			}
			
			//check if method name matches with any of the specified prefix
			for(String prefix: this.prefixes)
			{
				if(methodName.startsWith(prefix))
				{
					return true;
				}
			}
			
			//if no prefix condition matched, return mismatch
			return false;
		}
	}
	
	private static RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	/**
	 * Maintains list of supported executor details
	 */
	private List<ExecutorDetails> executorDetailsLst = new ArrayList<>();
	
	/**
	 * List of annotation mappings to be used to match repository methods to 
	 * executor
	 */
	private Map<Class<? extends Annotation>, ExecutorDetails> annotationToDetails = new HashMap<>();
	
	private PersistenceExecutionContext persistenceExecutionContext;
	
	public ExecutorFactory(PersistenceExecutionContext persistenceExecutionContext)
	{
		registerDefaultExecutors();
		
		this.persistenceExecutionContext = persistenceExecutionContext;
	}
	
	/**
	 * Gets the persistence execution context.
	 *
	 * @return the persistence execution context
	 */
	public PersistenceExecutionContext getPersistenceExecutionContext()
	{
		return persistenceExecutionContext;
	}
	
	/**
	 * Registers default executors
	 */
	protected void registerDefaultExecutors()
	{
		registerExecutor(AggregateQueryExecutor.class);
		registerExecutor(FinderQueryExecutor.class);
		registerExecutor(SearchQueryExecutor.class);
		registerExecutor(SaveQueryExecutor.class);
		registerExecutor(DeleteQueryExecutor.class);
		registerExecutor(UpdateQueryExecutor.class);
		
		registerExecutor(NativeQueryExecutor.class);
	}

	/**
	 * Registers specified executor type
	 * @param executorType
	 */
	public void registerExecutor(Class<? extends QueryExecutor> executorType)
	{
		QueryExecutorPattern executorPattern = executorType.getAnnotation(QueryExecutorPattern.class);
		
		//if QueryExecutorPattern is not specified on target type
		if(executorPattern == null)
		{
			throw new IllegalArgumentException("Specified executor-type is not annotated with @QueryExecutorPattern - " + executorType.getName());
		}
		
		//fetch prefix match conditions
		String prefixes[] = executorPattern.prefixes();
		prefixes = prefixes.length == 0 ? null : prefixes;

		//fetch prefix conditions that should not match
		String excludePrefixes[] = executorPattern.excludePrefixes();
		excludePrefixes = excludePrefixes.length == 0 ? null : excludePrefixes;

		//check if executor can be matched using annotation
		Class<? extends Annotation> annotationType = executorPattern.annotatedWith();
		
		if(prefixes == null && Annotation.class.equals(annotationType))
		{
			throw new IllegalArgumentException("Neither prefix not annotated-with is specified in @QueryExecutorPattern annotation of - " + executorType.getName());
		}
		
		//ensure required constructor is defined and get it for instance creation
		Constructor<?> constructor = null;
		
		try
		{
			constructor = executorType.getConstructor(Class.class, Method.class, EntityDetails.class);
		}catch(NoSuchMethodException ex)
		{
			throw new IllegalArgumentException("No constructor of type <init>(Class<?> repositoryType, Method method, EntityDetails entityDetails) "
					+ "is defined in specified executor type: " + executorType.getName());
		}
		
		ExecutorDetails executorDetails = new ExecutorDetails(constructor, prefixes, excludePrefixes); 
		this.executorDetailsLst.add(executorDetails);
		
		annotationToDetails.put(annotationType, executorDetails);
	}
	
	/**
	 * For given "repositoryType" and for given repositoryType's method "method" fetches QueryExecutor 
	 * @param repositoryType
	 * @param method
	 * @param entityDetails
	 * @return
	 */
	public QueryExecutor getQueryExecutor(Class<?> repositoryType, Method method, EntityDetails entityDetails)
	{
		//Annotation annotaions[] = method.getAnnotations();
		ExecutorDetails details = null;
		Annotation annotation = null;
		
		//based on annotations, check if match between repository method and executor can be done
		for(Class<? extends Annotation> annotaionType: annotationToDetails.keySet())
		{
			annotation = recursiveAnnotationFactory.findAnnotationRecursively(method, annotaionType);
			
			if(annotation == null)
			{
				continue;
			}
			
			details = annotationToDetails.get(annotaionType);
			
			if(details != null)
			{
				return details.newQueryExecutor(persistenceExecutionContext, repositoryType, method, entityDetails);
			}
		}
		/*
		for(Annotation annotaion: annotaions)
		{
			details = annotationToDetails.get(annotaion.annotationType());
			
			if(details != null)
			{
				return details.newQueryExecutor(persistenceExecutionContext, repositoryType, method, entityDetails);
			}
		}
		*/
		
		//if match can not be done using annotation
		String methodName = method.getName();
		
		//try if match can be done based on method name prefix conditions
		for(ExecutorDetails executorDetails: this.executorDetailsLst)
		{
			if(executorDetails.isMatchingMethodName(methodName))
			{
				return executorDetails.newQueryExecutor(persistenceExecutionContext, repositoryType, method, entityDetails);
			}
		}
		
		return null;
	}
}
