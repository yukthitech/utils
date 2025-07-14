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
package com.yukthitech.persistence;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.executors.QueryExecutor;

/**
 * Specifies repository methods meant for internal use by fw-data
 * @author akiran
 */
public interface IInternalRepository
{
	/**
	 * Drops corresponding entity table 
	 */
	public void dropEntityTable();
	
	/**
	 * Gets the actual repository type of this instance
	 * @return
	 */
	public Class<?> getRepositoryType();

	/**
	 * Fetches parent repository factory.
	 * @return  parent repository factory.
	 */
	public RepositoryFactory getRepositoryFactory();
	
	/**
	 * Fetches the data store used by this repository.
	 * @return underlying data store.
	 */
	public IDataStore getDataStore();
	
	/**
	 * Executes the specified query executor with specified params and returns the result.
	 * @param queryExecutor executor to execute
	 * @param params params to be passed for execution
	 * @return query executor result
	 */
	public Object executeQueryExecutor(QueryExecutor queryExecutor, Object... params);
}
