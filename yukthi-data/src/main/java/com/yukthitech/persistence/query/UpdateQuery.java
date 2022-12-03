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

/**
 * Represents an update query to be executed.
 * @author akiran
 */
public class UpdateQuery extends AbstractConditionalQuery implements IOrderedQuery
{
	/**
	 * Columns details to be updated.
	 */
	private List<UpdateColumnParam> columns;
	
	/**
	 * fields in which results should be ordered. Helpful when updating
	 * key column (order by helps in avoiding unique constraint issues).
	 */
	private List<QueryResultField> orderByFields;

	/**
	 * Instantiates a new update query.
	 *
	 * @param entityDetails the entity details
	 */
	public UpdateQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
	}

	/** 
	 * Adds value to {@link #columns Columns}
	 *
	 * @param column column to be added
	 */
	public void addColumn(UpdateColumnParam column)
	{
		if(columns == null)
		{
			columns = new ArrayList<>();
		}

		columns.add(column);
	}

	/**
	 * Gets the columns details to be updated.
	 *
	 * @return the columns details to be updated
	 */
	public List<UpdateColumnParam> getColumns()
	{
		return columns;
	}

	/**
	 * Gets the fields in which results should be ordered. Helpful when updating key column (order by helps in avoiding unique constraint issues).
	 *
	 * @return the fields in which results should be ordered
	 */
	@Override
	public List<QueryResultField> getOrderByFields()
	{
		return orderByFields;
	}

	/**
	 * Sets the fields in which results should be ordered. Helpful when updating key column (order by helps in avoiding unique constraint issues).
	 *
	 * @param orderByFields the new fields in which results should be ordered
	 */
	public void setOrderByFields(List<QueryResultField> orderByFields)
	{
		this.orderByFields = orderByFields;
	}
	
	/**
	 * Adds specified order by field to the query.
	 * @param orderByField Field to add
	 */
	@Override
	public void addOrderByField(QueryResultField orderByField)
	{
		if(this.orderByFields == null)
		{
			this.orderByFields = new ArrayList<>();
		}
		
		this.orderByFields.add(orderByField);
	}

	@Override
	public void clearConditions()
	{
		super.clearConditions();
		
		if(this.orderByFields != null)
		{
			this.orderByFields.clear();
		}
	}
	
	
}
