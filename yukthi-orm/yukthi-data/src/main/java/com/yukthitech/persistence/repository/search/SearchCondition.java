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
package com.yukthitech.persistence.repository.search;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;

/**
 * Dynamic condition for search query
 * 
 * @author akiran
 */
public class SearchCondition
{
	/**
	 * Entity field expression
	 */
	private String field;

	/**
	 * Condition operator
	 */
	private Operator operator;

	/**
	 * Value for condition comparison
	 */
	private Object value;

	/**
	 * Join operator for the condition
	 */
	private JoinOperator joinOperator;

	/**
	 * Conditions grouped with current condition
	 */
	private List<SearchCondition> groupedConditions;
	
	/**
	 * Indicates whether this condition can hold null value
	 */
	private boolean nullable;
	
	/**
	 * Indicates the condition should be case insensitive
	 */
	private boolean ignoreCase;

	/**
	 * Instantiates a new search condition.
	 */
	public SearchCondition()
	{}

	/**
	 * Instantiates a new search condition.
	 *
	 * @param field the field
	 * @param operator the operator
	 * @param value the value
	 */
	public SearchCondition(String field, Operator operator, Object value)
	{
		this(JoinOperator.AND, field, operator, value);
	}

	/**
	 * Instantiates a new search condition.
	 *
	 * @param field the field
	 * @param operator the operator
	 * @param value the value
	 * @param nullable if condition can allow null values
	 */
	public SearchCondition(String field, Operator operator, Object value, boolean nullable)
	{
		this(JoinOperator.AND, field, operator, value, nullable);
	}

	/**
	 * Instantiates a new search condition.
	 *
	 * @param joinOperator the join operator
	 * @param field the field
	 * @param operator the operator
	 * @param value the value
	 */
	public SearchCondition(JoinOperator joinOperator, String field, Operator operator, Object value)
	{
		this(joinOperator, field, operator, value, false);
	}

	/**
	 * Instantiates a new search condition.
	 *
	 * @param joinOperator the join operator
	 * @param field the field
	 * @param operator the operator
	 * @param value the value
	 * @param nullable if condition can allow null values
	 */
	public SearchCondition(JoinOperator joinOperator, String field, Operator operator, Object value, boolean nullable)
	{
		this.field = field;
		this.operator = operator;
		this.joinOperator = joinOperator;
		this.nullable = nullable;
		
		this.setValue(value);
	}

	/**
	 * @return the {@link #field field}
	 */
	public String getField()
	{
		return field;
	}

	/**
	 * @param field
	 *            the {@link #field field} to set
	 */
	public void setField(String field)
	{
		this.field = field;
	}

	/**
	 * @return the {@link #operator operator}
	 */
	public Operator getOperator()
	{
		return operator;
	}

	/**
	 * @param operator
	 *            the {@link #operator operator} to set
	 */
	public void setOperator(Operator operator)
	{
		this.operator = operator;
	}

	/**
	 * @return the {@link #value value}
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the {@link #value value} to set
	 */
	public void setValue(Object value)
	{
		//if value is subquery
		if(value instanceof SearchQuery)
		{
			SearchQuery subquery = (SearchQuery) value;
			
			if(subquery.getSubentityType() == null)
			{
				throw new IllegalArgumentException("No entity type specified for subquery");
			}
			
			if(subquery.getAdditionalEntityFields() == null || subquery.getAdditionalEntityFields().size() != 1)
			{
				throw new IllegalArgumentException("Zero or more than one result field is configured in specified subquery");
			}
		}
		
		this.value = value;
	}

	/**
	 * Gets the join operator for the condition.
	 *
	 * @return the join operator for the condition
	 */
	public JoinOperator getJoinOperator()
	{
		return joinOperator;
	}

	/**
	 * Sets the join operator for the condition.
	 *
	 * @param joinOperator the new join operator for the condition
	 */
	public void setJoinOperator(JoinOperator joinOperator)
	{
		this.joinOperator = joinOperator;
	}

	/**
	 * Adds value to {@link #groupedConditions groupedConditions}
	 *
	 * @param condition
	 *            condition to be added
	 *            
	 * @return Returns current conditions so that multiple conditions can be added with ease
	 */
	public SearchCondition addCondition(SearchCondition condition)
	{
		if(groupedConditions == null)
		{
			groupedConditions = new ArrayList<SearchCondition>();
		}

		groupedConditions.add(condition);
		
		return this;
	}

	/**
	 * Gets the conditions grouped with current condition.
	 *
	 * @return the conditions grouped with current condition
	 */
	public List<SearchCondition> getGroupedConditions()
	{
		return groupedConditions;
	}

	/**
	 * Sets the conditions grouped with current condition.
	 *
	 * @param groupedConditions the new conditions grouped with current condition
	 */
	public void setGroupedConditions(List<SearchCondition> groupedConditions)
	{
		this.groupedConditions = groupedConditions;
	}
	
	/**
	 * Checks if is indicates whether this condition can hold null value.
	 *
	 * @return the indicates whether this condition can hold null value
	 */
	public boolean isNullable()
	{
		return nullable;
	}

	/**
	 * Sets the indicates whether this condition can hold null value.
	 *
	 * @param nullable the new indicates whether this condition can hold null value
	 */
	public void setNullable(boolean nullable)
	{
		this.nullable = nullable;
	}

	/**
	 * Checks if is indicates the condition should be case insensitive.
	 *
	 * @return the indicates the condition should be case insensitive
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	/**
	 * Sets the indicates the condition should be case insensitive.
	 *
	 * @param ignoreCase the new indicates the condition should be case insensitive
	 */
	public void setIgnoreCase(boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append(joinOperator).append(" ").append(field).append(" ").append(operator).append(" ").append(value);

		builder.append("]");
		return builder.toString();
	}
}
