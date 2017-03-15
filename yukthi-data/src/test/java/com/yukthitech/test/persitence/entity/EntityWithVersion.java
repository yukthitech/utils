package com.yukthitech.test.persitence.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Table(name = "ENT_WITH_VER")
public class EntityWithVersion
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Version
	private Integer version = 1;

	private int age;

	public EntityWithVersion()
	{}

	public EntityWithVersion(int age)
	{
		this.age = age;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}
	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 * @return the {@link #version version}
	 */
	public Integer getVersion()
	{
		return version;
	}
	
	/**
	 * @param version the {@link #version version} to set
	 */
	public void setVersion(Integer version)
	{
		this.version = version;
	}
}
