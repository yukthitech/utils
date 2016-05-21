package com.yukthi.indexer;

import java.util.List;

public class IndexSearchResult
{
	/**
	 * Search result with details.
	 * @author akiran
	 */
	public static class ResultDetails
	{
		/**
		 * Actual result.
		 */
		private Object result;
		
		/**
		 * Score of the search result.
		 */
		private double score;
		
		public ResultDetails(Object result, double score)
		{
			this.result = result;
			this.score = score;
		}

		public Object getResult()
		{
			return result;
		}

		public double getScore()
		{
			return score;
		}
	}
	
	/**
	 * List of matching results.
	 */
	private List<Object> results;
	
	/**
	 * List of result s with details.
	 */
	private List<ResultDetails> resultDetails;
	
}
