package com.yukthitech.indexer.es;

import java.util.List;

import com.yukthitech.indexer.search.ConditionOperator;
import com.yukthitech.indexer.search.SearchCondition;
import com.yukthitech.indexer.search.SearchQuery;

@SearchQuery(indexType = TestBean.class, ignoreScore = false)
public class TestBeanSearchQuery1
{
	@SearchCondition(op = ConditionOperator.EQ)
	private String name;
	
	@SearchCondition
	private String text;
	
	@SearchCondition
	private List<String> keys;
	
	@SearchCondition(op = ConditionOperator.GTE)
	private Integer value;

	public TestBeanSearchQuery1(String name, String text, List<String> keys, Integer value)
	{
		this.name = name;
		this.text = text;
		this.keys = keys;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public List<String> getKeys()
	{
		return keys;
	}

	public void setKeys(List<String> keys)
	{
		this.keys = keys;
	}

	public Integer getValue()
	{
		return value;
	}

	public void setValue(Integer value)
	{
		this.value = value;
	}
}
