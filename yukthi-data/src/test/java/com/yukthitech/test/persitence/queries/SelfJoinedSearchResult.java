package com.yukthitech.test.persitence.queries;

import com.yukthitech.persistence.repository.annotations.Field;

public class SelfJoinedSearchResult
{
	@Field("id")
	private long id;

	@Field("name")
	private String name;

	@Field("createdBy.name")
	private String createdBy;

	@Field("updatedBy.name")
	private String updatedBy;

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

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

}
