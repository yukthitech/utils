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
