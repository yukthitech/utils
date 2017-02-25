package com.yukthitech.indexer.es;

public enum EsDataType
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

	private EsDataType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
