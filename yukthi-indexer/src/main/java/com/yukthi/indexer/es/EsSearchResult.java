package com.yukthi.indexer.es;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class EsSearchResult
{
	@JsonIgnoreProperties(ignoreUnknown = true)
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
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class HitsWrapper
	{
		private int total;
		
		private double maxScore;
		
		private List<Hit> hits;

		public List<Hit> getHits()
		{
			return hits;
		}

		public void setHits(List<Hit> hits)
		{
			this.hits = hits;
		}

		public int getTotal()
		{
			return total;
		}

		public void setTotal(int total)
		{
			this.total = total;
		}

		@JsonProperty("max_score")
		public double getMaxScore()
		{
			return maxScore;
		}

		public void setMaxScore(double maxScore)
		{
			this.maxScore = maxScore;
		}
	}

	private HitsWrapper hits;

	public HitsWrapper getHits()
	{
		return hits;
	}

	public void setHits(HitsWrapper hits)
	{
		this.hits = hits;
	}
	
	public List<Hit> finalHits()
	{
		if(hits == null)
		{
			return null;
		}
		
		return hits.hits;
	}
}
