package com.yukthi.indexer.es;

import java.util.List;

import com.yukthi.indexer.IndexField;
import com.yukthi.indexer.IndexType;

public class TestBean
{
	@IndexField(IndexType.NOT_ANALYZED)
	private String name;
	
	@IndexField(IndexType.ANALYZED)
	private String text;
	
	@IndexField(IndexType.NOT_ANALYZED)
	private List<String> keys;
	
	@IndexField
	private int value;
	
	public TestBean()
	{}

	public TestBean(String name, String text, List<String> keys, int value)
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

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}
}
