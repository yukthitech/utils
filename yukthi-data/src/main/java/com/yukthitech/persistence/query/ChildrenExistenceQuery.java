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

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.persistence.EntityDetails;

public class ChildrenExistenceQuery extends Query implements IChildQuery
{
	private List<QueryCondition> childConditions = new ArrayList<>();
	private List<QueryCondition> parentConditions = new ArrayList<>();

	private EntityDetails childEntityDetails;
	private EntityDetails parentEntityDetails;

	private List<String> childColumns = new ArrayList<>();
	private List<String> parentColumns = new ArrayList<>();
	
	public ChildrenExistenceQuery(EntityDetails childEntityDetails, EntityDetails parentEntityDetails)
	{
		super(childEntityDetails);

		this.childEntityDetails = childEntityDetails;
		this.parentEntityDetails = parentEntityDetails;
	}

	public String getChildTableName()
	{
		return childEntityDetails.getTableName();
	}

	public String getParentTableName()
	{
		return parentEntityDetails.getTableName();
	}

	/**
	 * Adds value to {@link #childConditions Conditions}
	 *
	 * @param condition condition to be added
	 */
	public void addChildCondition(QueryCondition condition)
	{
		if(childConditions == null)
		{
			childConditions = new ArrayList<QueryCondition>();
		}

		childConditions.add(condition);
	}

	public List<QueryCondition> getChildConditions()
	{
		return childConditions;
	}

	/** 
	 * Adds value to {@link #parentConditions parent Conditions}
	 *
	 * @param condition condition to be added
	 */
	public void addParentCondition(QueryCondition condition)
	{
		if(parentConditions == null)
		{
			parentConditions = new ArrayList<QueryCondition>();
		}

		parentConditions.add(condition);
	}

	public List<QueryCondition> getParentConditions()
	{
		return parentConditions;
	}

	public void addMapping(String childColumn, String parentColumn)
	{
		childColumns.add(childColumn);
		parentColumns.add(parentColumn);
	}

	public List<String> getChildColumns()
	{
		return childColumns;
	}
	
	public List<String> getParentColumns()
	{
		return parentColumns;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[Child Conditions: ");

		toString(childConditions, builder);
		
		builder.append(" || Parent Conditions: ");

		toString(parentConditions, builder);

		builder.append("]");
		return builder.toString();
	}
}
