package com.yukthitech.swing.combo;

import java.util.List;

/**
 * Used to filter items.
 * @author akiran
 */
public interface ISearchableDataProvider<E>
{
	/**
	 * Fetches items based on specified filter.
	 *
	 * @param filter the filter
	 * @return matching list of items.
	 */
	public List<E> fetchItems(String filter);
}
