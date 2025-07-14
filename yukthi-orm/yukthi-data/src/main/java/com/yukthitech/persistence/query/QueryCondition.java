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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents a condition in condition based query
 * 
 * @author akiran
 */
public class QueryCondition implements Cloneable
{
	/**
	 * Table (code) to be used for this condition
	 */
	private String tableCode;

	/**
	 * Column to be used for this condition
	 */
	private String column;

	/**
	 * Condition operator
	 */
	private Operator operator = Operator.EQ;

	/**
	 * Value for the condition
	 */
	private Object value;

	/**
	 * Joining operator
	 */
	private JoinOperator joinOperator = JoinOperator.AND;

	/**
	 * Conditions grouped with current condition
	 */
	private List<QueryCondition> groupedConditions;

	/**
	 * Indicates whether the condition should be case insensitive
	 */
	private boolean ignoreCase = false;
	
	/**
	 * Subquery to be used as part of this condition.
	 */
	private Subquery subquery;
	
	/**
	 * Data type of underlying field.
	 */
	private DataType dataType = DataType.UNKNOWN;

	/**
	 * Instantiates a new query condition.
	 *
	 * @param tableCode
	 *            the table code
	 * @param column
	 *            the column
	 * @param operator
	 *            the operator
	 * @param value
	 *            the value
	 * @param joinOperator
	 *            the join operator
	 */
	public QueryCondition(String tableCode, String column, Operator operator, Object value, JoinOperator joinOperator, boolean ignoreCase)
	{
		this.tableCode = tableCode;
		this.column = column;
		this.operator = operator;
		this.value = checkForEnum( value );
		this.joinOperator = joinOperator;
		this.ignoreCase = ignoreCase;
	}
	
	/**
	 * If specified value is enum, converts enum to string and returns the same. 
	 * @param value value to check
	 * @return converted value.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object checkForEnum(Object value)
	{
		//if value is enum
		if(value instanceof Enum)
		{
			return value.toString();
		}
		
		//if value is collection
		if(value instanceof Collection)
		{
			Collection<Object> newCol = new ArrayList<>();
			Collection<Object> oldCol = (Collection) value;
			boolean enumFound = false;
			
			//check through elements of collection
			for(Object elem : oldCol)
			{
				if(elem instanceof Enum)
				{
					newCol.add(elem.toString());
					enumFound = true;
				}
				else
				{
					newCol.add(elem);
				}
			}
			
			//if atleast one enum is found, return converted collection
			if(enumFound)
			{
				return newCol;
			}
			
			//if no enum is found, return actual collection.
			return oldCol;
		}
		
		return value;
	}

	/**
	 * @return the {@link #tableCode tableCode}
	 */
	public String getTableCode()
	{
		return tableCode;
	}

	/**
	 * @param tableCode
	 *            the {@link #tableCode tableCode} to set
	 */
	public void setTableCode(String tableCode)
	{
		this.tableCode = tableCode;
	}

	/**
	 * @return the {@link #column column}
	 */
	public String getColumn()
	{
		return column;
	}

	/**
	 * @param column
	 *            the {@link #column column} to set
	 */
	public void setColumn(String column)
	{
		this.column = column;
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
		if(ignoreCase && (value instanceof String))
		{
			value = ((String)value).toLowerCase();
		}
		
		return value;
	}

	/**
	 * @param value
	 *            the {@link #value value} to set
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * Gets the joining operator.
	 *
	 * @return the joining operator
	 */
	public JoinOperator getJoinOperator()
	{
		return joinOperator;
	}

	/**
	 * Sets the joining operator.
	 *
	 * @param joinOperator
	 *            the new joining operator
	 */
	public void setJoinOperator(JoinOperator joinOperator)
	{
		this.joinOperator = joinOperator;
	}

	/**
	 * Gets the conditions grouped with current condition.
	 *
	 * @return the conditions grouped with current condition
	 */
	public List<QueryCondition> getGroupedConditions()
	{
		return groupedConditions;
	}

	/**
	 * Sets the conditions grouped with current condition.
	 *
	 * @param groupedConditions
	 *            the new conditions grouped with current condition
	 */
	public void setGroupedConditions(List<QueryCondition> groupedConditions)
	{
		this.groupedConditions = groupedConditions;
	}

	/**
	 * Adds the grouped condition.
	 *
	 * @param condition the condition
	 */
	public void addGroupedCondition(QueryCondition condition)
	{
		if(groupedConditions == null)
		{
			this.groupedConditions = new ArrayList<>();
		}

		this.groupedConditions.add(condition);
	}

	/**
	 * Checks if is indicates whether the condition should be case insensitive.
	 *
	 * @return the indicates whether the condition should be case insensitive
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	/**
	 * Sets the indicates whether the condition should be case insensitive.
	 *
	 * @param ignoreCase the new indicates whether the condition should be case insensitive
	 */
	public void setIgnoreCase(boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
	}

	/**
	 * Gets the subquery to be used as part of this condition.
	 *
	 * @return the subquery to be used as part of this condition
	 */
	public Subquery getSubquery()
	{
		return subquery;
	}

	/**
	 * Sets the subquery to be used as part of this condition.
	 *
	 * @param subquery the new subquery to be used as part of this condition
	 */
	public void setSubquery(Subquery subquery)
	{
		this.subquery = subquery;
	}
	
	/**
	 * Populates the parameters required by this condition.
	 * @param params List to which parameters needs to be populated.
	 */
	public void fetchQueryParameters(List<Object> params)
	{
		if(subquery != null)
		{
			subquery.fetchQueryParameters(params);
		}
		else
		{
			if(!isMultiValued())
			{
				//when the check is for null, dont add any param
				if(operator == Operator.EQ && value == null)
				{
					return;
				}
				
				params.add(value);
			}
			else
			{
				params.addAll(getMultiValues());
			}
		}
	}
	
	/**
	 * Returns true if the operator is IN.
	 * @return true if operator is multi valued
	 */
	public boolean isMultiValued()
	{
		return ((operator == Operator.IN || operator == Operator.NOT_IN) && (value != null));
	}
	
	/**
	 * Converts the value into list.
	 * @return value as multi values.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> getMultiValues()
	{
		if(operator != Operator.IN)
		{
			throw new InvalidStateException("Invoked multi value fetch on non IN operator condition");
		}
		
		if(value instanceof Collection)
		{
			return new ArrayList<>((Collection) value);
		}
		
		if(value == null)
		{
			return null;
		}
		
		return Arrays.asList(value);
	}
	
	/**
	 * Gets the data type of underlying field.
	 *
	 * @return the data type of underlying field
	 */
	public DataType getDataType()
	{
		return dataType;
	}

	/**
	 * Sets the data type of underlying field.
	 *
	 * @param dataType the new data type of underlying field
	 */
	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}
	
	/**
	 * Gets the data type name.
	 *
	 * @return the data type name
	 */
	public String getDataTypeName()
	{
		return dataType.name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public QueryCondition clone()
	{
		try
		{
			return (QueryCondition) super.clone();
		} catch(CloneNotSupportedException ex)
		{
			throw new IllegalStateException("An error occurred while cloding query condition", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[");

		builder.append(joinOperator).append(" ");
		
		if(groupedConditions != null)
		{
			builder.append("( ");
		}
		
		builder.append(tableCode).append(".").append(column).append(" ");
		
		if(subquery != null)
		{
			builder.append(operator).append(" ").append(subquery.toString());
		}
		else
		{
			builder.append(operator).append(" ").append(value);
		}
		
		if(groupedConditions != null)
		{
			for(QueryCondition subcond : groupedConditions)
			{
				builder.append(" ").append(subcond.getJoinOperator()).append(" ").append(subcond);
			}
			
			builder.append(" )");
		}

		return builder.toString();
	}
}
