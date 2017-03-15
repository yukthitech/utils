package com.yukthitech.test.persitence.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "ENT_UPD_OP")
public class EntityWithUpdateOperator
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private int age;

	public EntityWithUpdateOperator()
	{}

	public EntityWithUpdateOperator(int age)
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

}
