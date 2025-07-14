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

/**
 * Represents filter for filtering data while fetching data from repository. The filter also
 * can decide when to stop the consumption of data by returning appropriate action.
 * 
 * Note: The return type of the method should match with the data filter.
 * 
 * @author akiran
 * @param <T> Data expected from the repository.
 */
public interface IDataFilter<T>
{
	/**
	 * Called by the repository fetcher/finder method for every record or data object being fetched.
	 * @param data data to be checked for filtering.
	 * @return action to be taken on current data and also on future fetch. If null, it will be treated as {@link FilterAction#ACCEPT}
	 */
	public FilterAction filter(T data);
}
