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
package com.yukthitech.utils.beans;

import java.util.Date;

import com.yukthitech.utils.annotations.PropertyMapping;

/**
 * @author akiran
 */
public class EntityBean
{
	private Long id;

	@PropertyMapping(type = ModelBean.class, from = "name")
	private String entityName;
	
	@PropertyMapping(type = ModelBean.class, from = "subpropId", subproperty = "id")
	private EntitySubbean subprop;
	
	private Date createdOn;
	
	private FileEntity file;

	public EntityBean()
	{}
	
	public EntityBean(Long id, String name, EntitySubbean subprop, Date createdOn, FileEntity file)
	{
		this.id = id;
		this.entityName = name;
		this.subprop = subprop;
		this.createdOn = createdOn;
		this.file = file;
	}
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	public EntitySubbean getSubprop()
	{
		return subprop;
	}

	public void setSubprop(EntitySubbean subprop)
	{
		this.subprop = subprop;
	}

	public Date getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	public FileEntity getFile()
	{
		return file;
	}

	public void setFile(FileEntity file)
	{
		this.file = file;
	}
}
