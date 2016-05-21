package com.yukthi.indexer.es;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

class EsSearchResult
{
	public static class Hit
	{
		private String id;
		private double score;
		private Map<String, Object> source;

		@JsonProperty("_id")
		public String getId()
		{
			return id;
		}

		public void setId(String id)
		{
			this.id = id;
		}

		@JsonProperty("_score")
		public double getScore()
		{
			return score;
		}

		public void setScore(double score)
		{
			this.score = score;
		}

		@JsonProperty("_source")
		public Map<String, Object> getSource()
		{
			return source;
		}

		public void setSource(Map<String, Object> source)
		{
			this.source = source;
		}

	}

	private List<Hit> hits;

	public List<Hit> getHits()
	{
		return hits;
	}

	public void setHits(List<Hit> hits)
	{
		this.hits = hits;
	}
}
