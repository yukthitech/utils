package com.yukthitech.indexer.es;

import java.util.Map;

import com.yukthitech.indexer.search.JoinOperator;
import com.yukthitech.indexer.search.SearchCondition;
import com.yukthitech.indexer.search.SearchQuery;

@SearchQuery(indexType = BeanWithMap.class)
public class BeanWithMapQuery
{
	@SearchCondition
	private Map<String, String> map;
	
	@SearchCondition(field = "map", joinOperator = JoinOperator.SHOULD)
	private Map<String, String> mustMap;

	public BeanWithMapQuery()
	{}
	
	public BeanWithMapQuery(Map<String, String> map)
	{
		this.map = map;
	}

	public BeanWithMapQuery(Map<String, String> map, Map<String, String> mustMap)
	{
		this.map = map;
		this.mustMap = mustMap;
	}

	public Map<String, String> getMap()
	{
		return map;
	}

	public void setMap(Map<String, String> map)
	{
		this.map = map;
	}

	public Map<String, String> getMustMap()
	{
		return mustMap;
	}

	public void setMustMap(Map<String, String> mustMap)
	{
		this.mustMap = mustMap;
	}
}
