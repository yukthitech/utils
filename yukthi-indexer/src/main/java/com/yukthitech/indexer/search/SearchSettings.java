package com.yukthitech.indexer.search;

/**
 * Search settings to be used during search operations.
 * @author akiran
 */
public class SearchSettings
{
	/**
	 * Count to which results should be limited. Default is all results (Integer.MAX_VALUE).
	 */
	private int resultsLimit = Integer.MAX_VALUE;

	/**
	 * Gets the count to which results should be limited. Default is all results (Integer.MAX_VALUE).
	 *
	 * @return the count to which results should be limited
	 */
	public int getResultsLimit()
	{
		return resultsLimit;
	}

	/**
	 * Sets the count to which results should be limited. Default is all results (Integer.MAX_VALUE).
	 *
	 * @param resultsLimit the new count to which results should be limited
	 */
	public void setResultsLimit(int resultsLimit)
	{
		this.resultsLimit = resultsLimit;
	}
}
