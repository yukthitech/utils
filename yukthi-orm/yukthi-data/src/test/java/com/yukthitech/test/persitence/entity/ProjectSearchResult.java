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

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;

/**
 * @author akiran
 *
 */
public class ProjectSearchResult implements IDynamicSearchResult
{
	@Field("name")
	private String name;
	
	private Map<String, String> extensionFields = new HashMap<String, String>();

	@Field("extensions.field3")
	private String extField3;

	@Field("extensions.field4")
	private String extField4;

	@Field("extensions.field5")
	private String extField5;

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.search.IDynamicSearchResult#addField(com.yukthitech.persistence.repository.search.DynamicResultField)
	 */
	@Override
	public void addField(DynamicResultField field)
	{
		extensionFields.put(field.getField(), (String)field.getValue());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, String> getExtensionFields()
	{
		return extensionFields;
	}

	public void setExtensionFields(Map<String, String> extensionFields)
	{
		this.extensionFields = extensionFields;
	}
	
	public String getExtensionField(String name)
	{
		return extensionFields.get(name);
	}

	public String getExtField3()
	{
		return extField3;
	}

	public void setExtField3(String extField3)
	{
		this.extField3 = extField3;
	}

	public String getExtField4()
	{
		return extField4;
	}

	public void setExtField4(String extField4)
	{
		this.extField4 = extField4;
	}

	public String getExtField5()
	{
		return extField5;
	}

	public void setExtField5(String extField5)
	{
		this.extField5 = extField5;
	}
}
