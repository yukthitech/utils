package com.fw.test.persitence.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.Extendable;
import com.yukthi.persistence.annotations.ExtendedFields;

@Table(name = "PROJECT")
@Extendable(count = 10, fieldSize = 30)
public class Project
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "NAME")
	private String name;
	
	/**
	 * Map to hold extension fields
	 */
	@ExtendedFields
	private Map<String, Object> extensions;
	
	public Project()
	{}
	
	public Project(String name, Map<String, Object> extensions)
	{
		this.name = name;
		this.extensions = extensions;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, Object> getExtensions()
	{
		return extensions;
	}

	public void setExtensions(Map<String, Object> extensions)
	{
		this.extensions = extensions;
	}
}
