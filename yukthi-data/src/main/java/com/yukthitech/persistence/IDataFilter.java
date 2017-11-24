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
