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

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.Extendable;
import com.yukthitech.persistence.annotations.ExtendedFields;

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
