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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;

@Table(name = "DATE_ENTITY")
public class DateDataEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "NAME")
	private String name;
	
	@Column(name = "FLD_DATE")
	@DataTypeMapping(type = DataType.DATE)
	private Date date;
	
	@Column(name = "FLD_DATE_TIME")
	private Date dateWithTime;
	
	public DateDataEntity()
	{}

	public DateDataEntity(String name, Date date, Date dateWithTime)
	{
		this.name = name;
		this.date = date;
		this.dateWithTime = dateWithTime;
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

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Date getDateWithTime()
	{
		return dateWithTime;
	}

	public void setDateWithTime(Date dateWithTime)
	{
		this.dateWithTime = dateWithTime;
	}
}
