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
package com.yukthitech.indexer;

import java.util.Collection;

import com.yukthitech.indexer.search.SearchSettings;

/**
 * Abstraction for indexer to index objects and find the objects already indexed.
 * @author akiran
 */
public interface IDataIndex
{
	/**
	 * Indexes the specified objects.
	 * @param objects objects to index
	 * @return map from input object to corresponding id
	 */
	public void indexObjects(Collection<? extends Object> objects);
	
	/**
	 * Finds the objects with specified query.
	 * @param query
	 */
	public <T> IndexSearchResult<T> search(Object query, SearchSettings searchSettings);
	
	/**
	 * Removes the object from index with specified id.
	 * @param deleteQuery Query to be used for deletion
	 */
	public long deleteObject(Object deleteQuery);
	
	/**
	 * Closes the current index.
	 */
	public void close();
}
