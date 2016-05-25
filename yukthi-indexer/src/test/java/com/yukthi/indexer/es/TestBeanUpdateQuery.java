package com.yukthi.indexer.es;

import java.util.List;

import com.yukthi.indexer.UpdateField;
import com.yukthi.indexer.UpdateOperation;

public class TestBeanUpdateQuery
{
	@UpdateField(op = UpdateOperation.APPEND)
	private String text;
	
	@UpdateField(name = "text", op = UpdateOperation.APPEND)
	private String extraText;

	@UpdateField(op = UpdateOperation.APPEND)
	private List<String> keys;
	
	@UpdateField(op = UpdateOperation.ADD)
	private int value;

	public TestBeanUpdateQuery(String text, String extraText, List<String> keys, int value)
	{
		this.text = text;
		this.extraText = extraText;
		this.keys = keys;
		this.value = value;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getExtraText()
	{
		return extraText;
	}

	public void setExtraText(String extraText)
	{
		this.extraText = extraText;
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
