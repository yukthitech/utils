package com.yukthitech.indexer;

import com.yukthitech.indexer.search.SearchSettings;

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
	 * @return Returns id of the object indexed
	 */
	public String indexObject(Object indexData, Object data);
	
	/**
	 * Queries the index with specified query to fetch the object to update.
	 * With the obtained object id, update data will be used to perform update.
	 * 
	 * If query resulted in more than one object, exception will be thrown.
	 * 
	 * @param indexType Index type in which object needs to be updated
	 * @param updateData Specified fields/data to update
	 * @param id Id of the object to update.
	 * /
	public void updateObject(Class<?> indexType, Object updateData, Object id);
	*/
	
	/**
	 * Updates the object with specified id in specified index with specified index data and data.
	 * @param id Id of the object to be updated
	 * @param indexData Index data to be updated
	 * @param data source object to be updated
	 */
	public void updateObject(Object id, Object indexData, Object data);
	
	/**
	 * Fetches object with specified id.
	 * @param indexType Index type to search
	 * @param id Id of the object to fetch.
	 * @return Matching object if any.
	 */
	public <T> T getObject(Class<?> indexType, Object id);
	
	/**
	 * Finds the objects with specified query.
	 * @param query
	 */
	public <T> IndexSearchResult<T> search(Object query, SearchSettings searchSettings);
	
	/**
	 * Removes the object from index with specified id.
	 * @param id
	 */
	public void deleteObject(Class<?> indexType, Object id);
	
	/**
	 * Removes all the data from index.
	 */
	public void clean();
}
