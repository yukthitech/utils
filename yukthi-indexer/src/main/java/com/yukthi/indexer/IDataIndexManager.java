package com.yukthi.indexer;

/**
 * Data index manager abstraction.
 * @author akiran
 */
public interface IDataIndexManager
{
	/**
	 * Gets data index with specified name.
	 * @param name Name of the index.
	 * @return Corresponding data index.
	 */
	public IDataIndex getIndex(String name);
}
