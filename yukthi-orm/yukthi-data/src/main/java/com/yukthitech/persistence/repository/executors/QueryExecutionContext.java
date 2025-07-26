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
package com.yukthitech.persistence.repository.executors;

import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Encapsulation of parameters and other context sent during query executor execution.
 * @author akiran
 */
public class QueryExecutionContext
{
	/**
	 * Conversion service to be used by query executors.
	 */
	private ConversionService conversionService;

	private RepositoryFactory repositoryFactory;
	
	/**
	 * Repository execution context.
	 */
	private Object repositoryExecutionContext;
	
	public QueryExecutionContext(ConversionService conversionService, RepositoryFactory repositoryFactory)
	{
		this.conversionService = conversionService;
		this.repositoryFactory = repositoryFactory;
	}
	
	public ConversionService getConversionService()
	{
		return conversionService;
	}
	
	public RepositoryFactory getRepositoryFactory()
	{
		return repositoryFactory;
	}
	
	public void setRepositoryExecutionContext(Object repositoryExecutionContext)
	{
		this.repositoryExecutionContext = repositoryExecutionContext;
	}
	
	public Object getRepositoryExecutionContext()
	{
		return repositoryExecutionContext;
	}
}
