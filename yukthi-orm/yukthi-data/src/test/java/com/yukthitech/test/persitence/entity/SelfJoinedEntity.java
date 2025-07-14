/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
