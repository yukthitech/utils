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
package com.yukthitech.persistence.query.data;

import java.util.Map;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.UniqueConstraintDetails;

public class UniqueConstraintStructure
{
	private String name;
	private String columns[];
	
	public UniqueConstraintStructure(EntityDetails entityDetails, UniqueConstraintDetails constraint, Map<String, String> fieldMapping)
	{
		this.name = constraint.getConstraintName();
		
		//FieldDetails fieldDetails = null;
		columns = new String[constraint.getFields().size()];
		
		int idx = 0;
		
		for(String field: constraint.getFields())
		{
			//fieldDetails = entityDetails.getFieldDetailsByField(field);
			columns[idx] = fieldMapping.get(field);
			
			idx++;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String[] getColumns()
	{
		return columns;
	}

	public void setColumns(String[] columns)
	{
		this.columns = columns;
	}

}
