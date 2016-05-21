package com.yukthi.indexer.es;

import com.yukthi.indexer.search.ConditionOperator;
import com.yukthi.indexer.search.SearchCondition;
import com.yukthi.indexer.search.SearchQuery;

@SearchQuery(indexType = TestBean.class, ignoreScore = false)
public class TestBeanSearchQuery1
{
	@SearchCondition(op = ConditionOperator.EQ)
	private String name;
	
	@SearchCondition
	private String text;
	
	public TestBeanSearchQuery1(String name, String text)
	{
		this.name = name;
		this.text = text;
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

}
