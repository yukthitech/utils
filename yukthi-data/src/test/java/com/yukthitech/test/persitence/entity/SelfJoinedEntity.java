package com.yukthitech.test.persitence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "SELF_JOINED")
public class SelfJoinedEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "NAME")
	private String name;

	@ManyToOne
	@Column(name = "CREATED_BY")
	private SelfJoinedEntity createdBy;

	@ManyToOne
	@Column(name = "UPDATED_BY")
	private SelfJoinedEntity updatedBy;

	public SelfJoinedEntity()
	{}
	
	public SelfJoinedEntity(String name, SelfJoinedEntity createdBy, SelfJoinedEntity updatedBy)
	{
		this.name = name;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
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

	public SelfJoinedEntity getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(SelfJoinedEntity createdBy)
	{
		this.createdBy = createdBy;
	}

	public SelfJoinedEntity getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(SelfJoinedEntity updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	
}
