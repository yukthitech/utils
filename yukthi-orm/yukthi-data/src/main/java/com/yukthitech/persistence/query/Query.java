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
package com.yukthitech.persistence.query;

import java.util.Iterator;
import java.util.List;

import com.yukthitech.persistence.EntityDetails;

public abstract class Query
{
	protected EntityDetails entityDetails;
	
	public Query(EntityDetails entityDetails)
	{
		this.entityDetails = entityDetails;
	}

	public EntityDetails getEntityDetails()
	{
		return entityDetails;
	}
	
	public String getTableName()
	{
		return entityDetails.getTableName();
	}
	
	public static void toString(List<QueryCondition> conditions, StringBuilder builder)
	{
		if(conditions == null || conditions.isEmpty())
		{
			builder.append("{}");
			return;
		}
		
		Iterator<QueryCondition> it = conditions.iterator();
		
		builder.append("{");
		
		while(it.hasNext())
		{
			builder.append(it.next());
			
			if(it.hasNext())
			{
				builder.append(" AND ");
			}
		}
		
		builder.append("}");
	}
}
