package com.yukthitech.autox.ide.rest;

public class MultiPart
{
	private String type;
	private String name;
	private String ContentType="application/json";
	private String value;
	public MultiPart(String type, String name, String contentType, String value)
	{
		super();
		this.type = type;
		this.name = name;
		this.ContentType = contentType;
		this.value = value;
	}
	
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getContentType()
	{
		return ContentType;
	}
	public void setContentType(String contentType)
	{
		ContentType = contentType;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}	
}