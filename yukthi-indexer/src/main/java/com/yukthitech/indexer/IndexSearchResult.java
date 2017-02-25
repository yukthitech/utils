package com.yukthitech.indexer;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class IndexSearchResult.
 */
public class IndexSearchResult<T>
{
	/**
	 * Search result with details.
	 * @author akiran
	 */
	public static class ResultDetails<T>
	{
		/**
		 * Actual result.
		 */
		private T result;
		
		/**
		 * Score of the search result.
		 */
		private double score;
		
		/**
		 * Instantiates a new result details.
		 */
		public ResultDetails()
		{}
		
		/**
		 * Instantiates a new result details.
		 *
		 * @param result the result
		 * @param score the score
		 */
		public ResultDetails(T result, double score)
		{
			this.result = result;
			this.score = score;
		}

		/**
		 * Gets the actual result.
		 *
		 * @return the actual result
		 */
		public T getResult()
		{
			return result;
		}

		/**
		 * Gets the score of the search result.
		 *
		 * @return the score of the search result
		 */
		public double getScore()
		{
			return score;
		}
	}
	
	/**
	 * List of matching results.
	 */
	private List<T> results;
	
	/**
	 * List of result s with details.
	 */
	private List<ResultDetails<T>> resultDetails;
	
	/**
	 * Adds the result.
	 *
	 * @param result the result
	 * @param score the score
	 */
	public void addResult(T result, double score)
	{
		if(results == null)
		{
			results = new ArrayList<>();
		}

		if(resultDetails == null)
		{
			resultDetails = new ArrayList<>();
		}
		
		results.add(result);
		resultDetails.add(new ResultDetails<T>(result, score));
	}

	/**
	 * Gets the list of matching results.
	 *
	 * @return the list of matching results
	 */
	public List<T> getResults()
	{
		return results;
	}

	/**
	 * Sets the list of matching results.
	 *
	 * @param results the new list of matching results
	 */
	public void setResults(List<T> results)
	{
		this.results = results;
	}

	/**
	 * Gets the list of result s with details.
	 *
	 * @return the list of result s with details
	 */
	public List<ResultDetails<T>> getResultDetails()
	{
		return resultDetails;
	}

	/**
	 * Sets the list of result s with details.
	 *
	 * @param resultDetails the new list of result s with details
	 */
	public void setResultDetails(List<ResultDetails<T>> resultDetails)
	{
		this.resultDetails = resultDetails;
	}
}
