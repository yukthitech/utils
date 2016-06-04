package com.yukthi.indexer.es;

import java.util.Map;

import com.yukthi.indexer.IndexField;
import com.yukthi.indexer.IndexType;

public class BeanWithMap
{
	@IndexField(IndexType.NOT_ANALYZED)
	private String name;
	
	@IndexField(IndexType.ANALYZED)
	private Map<String, String> map;

	public BeanWithMap()
	{}
	
	public BeanWithMap(String name, Map<String, String> map)
	{
		this.name = name;
		this.map = map;
	}
 
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, String> getMap()
	{
		return map;
	}

	public void setMap(Map<String, String> map)
	{
		this.map = map;
	}
}
