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

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.persistence.EntityDetails;

/**
 * Abstract class for condition based queries.
 * @author akiran
 */
public abstract class AbstractConditionalQuery extends Query implements IConditionalQuery
{
	/**
	 * Holds query result fields
	 */
	private List<QueryResultField> resultFields;
	
	/**
	 * Holds query conditions
	 */
	private List<QueryCondition> conditions = new ArrayList<>();
	
	/**
	 * Holds query join conditions
	 */
	private List<QueryJoinCondition> joinConditions = new ArrayList<>();
	
	/**
	 * Default table code to be used.
	 */
	private String defaultTableCode = "T0";
	
	/**
	 * Holds tables that need to be used in query
	 */
	//private List<QueryTable> tables;
	
	public AbstractConditionalQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fw.persistence.query.IConditionalQuery#addCondition(com.fw.
	 * persistence.query.ConditionParam)
	 */
	@Override
	public void addCondition(QueryCondition condition)
	{
		if(conditions == null)
		{
			conditions = new ArrayList<>();
		}
		
		conditions.add(condition);
	}
	
	/**
	 * Gets the holds query conditions.
	 *
	 * @return the holds query conditions
	 */
	public List<QueryCondition> getConditions()
	{
		return conditions;
	}
	
	/**
	 * Clears all previously added non-join conditions if any
	 */
	public void clearConditions()
	{
		if(conditions == null)
		{
			return;
		}
		
		conditions.clear();
	}
	
	/* (non-Javadoc)
	 * @see com.fw.persistence.query.IConditionalQuery#addJoinCondition(com.fw.persistence.query.QueryJoinCondition)
	 */
	@Override
	public void addJoinCondition(QueryJoinCondition condition)
	{
		joinConditions.add(condition);
	}
	
	/**
	 * Gets the holds query join conditions.
	 *
	 * @return the holds query join conditions
	 */
	public List<QueryJoinCondition> getJoinConditions()
	{
		return joinConditions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fw.persistence.query.IConditionalQuery#addResultField(com.fw.
	 * persistence.query.ResultField)
	 */
	@Override
	public void addResultField(QueryResultField resultField)
	{
		if(resultFields == null)
		{
			resultFields = new ArrayList<>();
		}
		
		resultFields.add(resultField);
	}
	
	/**
	 * Gets the holds query result fields.
	 *
	 * @return the holds query result fields
	 */
	public List<QueryResultField> getResultFields()
	{
		return resultFields;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fw.persistence.query.IConditionalQuery#addTable(java.lang.String,
	 * java.lang.String)
	 * /
	@Override
	public void addTable(QueryTable table)
	{
		if(tables == null)
		{
			tables = new ArrayList<>();
		}
		
		tables.add(table);
	}
	
	public List<QueryTable> getTables()
	{
		if(tables == null)
		{
			return Arrays.asList(new QueryTable(getTableName(), "T0"));
		}
		
		return tables;
	}
	*/
	
	/**
	 * Gets the default table code to be used.
	 *
	 * @return the default table code to be used
	 */
	public String getDefaultTableCode()
	{
		return defaultTableCode;
	}

	/**
	 * Sets the default table code to be used.
	 *
	 * @param defaultTableCode the new default table code to be used
	 */
	public void setDefaultTableCode(String defaultTableCode)
	{
		this.defaultTableCode = defaultTableCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");
		
		toStringPrefix(builder);

		if(CollectionUtils.isNotEmpty(this.resultFields))
		{
			builder.append("\n\tFields: ").append(this.resultFields);
		}
		
		/*
		if(CollectionUtils.isNotEmpty(this.tables))
		{
			builder.append("\n\tTables: ").append(this.tables);
		}
		*/
		
		if(CollectionUtils.isNotEmpty(this.joinConditions))
		{
			builder.append("\n\tJoins: ").append(this.joinConditions);
		}

		if(CollectionUtils.isNotEmpty(this.conditions))
		{
			builder.append("\n\tConditions: ").append(this.conditions);
		}

		builder.append("\n]");
		return builder.toString();
	}
	
	/**
	 * Can be overridden by child classes to add prefix to final tostring result.
	 * @param builder builder to which content should be added.
	 */
	protected void toStringPrefix(StringBuilder builder)
	{}

}
