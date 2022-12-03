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

/**
 * @author akiran
 *
 */
public class ModelBean
{
	private Long id;
	
	private String name;
	
	private long subpropId;

	private Date createdOn;
	
	private Date updatedOn;
	
	private FileModel file;

	public ModelBean()
	{}
	
	public ModelBean(Long id, String name, long subpropId, Date createdOn, Date updatedOn, FileModel file)
	{
		this.id = id;
		this.name = name;
		this.subpropId = subpropId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getSubpropId()
	{
		return subpropId;
	}

	public void setSubpropId(long subpropId)
	{
		this.subpropId = subpropId;
	}

	public Date getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn()
	{
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn)
	{
		this.updatedOn = updatedOn;
	}

	public FileModel getFile()
	{
		return file;
	}

	public void setFile(FileModel file)
	{
		this.file = file;
	}
}
