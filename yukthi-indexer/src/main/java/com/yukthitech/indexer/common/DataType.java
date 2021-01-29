package com.yukthitech.indexer.common;

public enum DataType
{
	BYTE("byte"),
	SHORT("short"),
	INTEGER("integer"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),
	BOOLEAN("boolean"),
	DATE("date"),
	STRING("string"),
	MAP("object"),
	OBJECT("object");
	
	private String name;

	private DataType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
