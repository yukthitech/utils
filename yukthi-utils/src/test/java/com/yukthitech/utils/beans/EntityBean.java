/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
