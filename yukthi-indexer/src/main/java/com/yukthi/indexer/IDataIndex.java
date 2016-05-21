package com.yukthi.indexer;

import com.yukthi.indexer.search.SearchSettings;

/**
 * Abstraction for indexer to index objects and find the objects already indexed.
 * @author akiran
 */
public interface IDataIndex
{
	/**
	 * Indexes the specified object.
	 * @param indexData Data to be used for indexing
	 * @param data Data to be stored for this index
	 */
	public void indexObject(Object indexData, Object data);
	
	/**
	 * Finds the objects with specified query.
	 * @param query
	 */
	public <T> IndexSearchResult<T> search(Object query, SearchSettings searchSettings);
	
	/**
	 * Removes all the data from index.
	 */
	public void clean();
}
