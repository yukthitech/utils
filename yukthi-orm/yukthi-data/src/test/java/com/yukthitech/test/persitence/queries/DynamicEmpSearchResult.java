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
package com.yukthitech.test.persitence.queries;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;

public class DynamicEmpSearchResult implements IDynamicSearchResult
{
	@Field("name")
	private String empName;
	
	@Field("age")
	private Integer age;
	
	private Map<String, Object> extraFields = new HashMap<String, Object>();

	/**
	 * @return the {@link #empName empName}
	 */
	public String getEmpName()
	{
		return empName;
	}

	/**
	 * @param empName the {@link #empName empName} to set
	 */
	public void setEmpName(String empName)
	{
		this.empName = empName;
	}

	/**
	 * @return the {@link #age age}
	 */
	public Integer getAge()
	{
		return age;
	}

	/**
	 * @param age the {@link #age age} to set
	 */
	public void setAge(Integer age)
	{
		this.age = age;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.search.IDynamicSearchResult#addField(com.yukthitech.persistence.repository.search.DynamicResultField)
	 */
	@Override
	public void addField(DynamicResultField field)
	{
		extraFields.put(field.getField(), field.getValue());
	}
	
	/**
	 * @return the {@link #extraFields extraFields}
	 */
	public Map<String, Object> getExtraFields()
	{
		return extraFields;
	}
}
