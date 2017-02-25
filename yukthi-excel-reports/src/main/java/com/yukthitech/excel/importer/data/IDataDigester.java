package com.yukthitech.excel.importer.data;

public interface IDataDigester<T>
{
	public void digest(T bean);
}
