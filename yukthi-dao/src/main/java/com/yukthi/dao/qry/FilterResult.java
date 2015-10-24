package com.yukthi.dao.qry;

import com.yukthi.ccg.util.ICacheableBean;
import com.yukthi.ccg.util.ObjectCacheFactory;

public class FilterResult implements ICacheableBean<FilterResult>
{
	private int result;
	private Object value;
	private String delimiter;

	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
	
	public String getDelimiter()
	{
		return delimiter;
	}

	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

	@Override
	public void reinitalize(ObjectCacheFactory<FilterResult> factory)
	{
		result = 0;
		value = null;
		delimiter = null;
	}
}
