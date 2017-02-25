package com.yukthitech.persistence.repository.executors.builder;

import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.query.IConditionalQuery;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;

/**
 * Condition details to be used in the query
 * 
 * @author akiran
 */
public class Condition
{
	/**
	 * Condition operator
	 */
	Operator operator;

	/**
	 * Index of the parameter defining this condition
	 */
	Integer index;

	/**
	 * Embedded property name to be used when condition object in use
	 */
	String embeddedProperty;

	/**
	 * Entity field expression
	 */
	String fieldExpression;

	/**
	 * Table on which this condition needs to be applied
	 */
	TableInfo table;

	/**
	 * Field details (with column name) that can be used for condition
	 */
	FieldDetails fieldDetails;

	/**
	 * Condition join operator
	 */
	JoinOperator joinOperator;
	
	/**
	 * Indicates the condition should be case insensitive
	 */
	boolean ignoreCase;

	boolean nullable;
	
	String defaultValue;
	
	boolean joiningField;
	
	/**
	 * Table data source required by this condition to be part of the query.
	 */
	ITableDataSource tableDataSource;

	public Condition(int index, String fieldExpression, Operator operator, JoinOperator joinOperator, boolean  nullable, boolean ignoreCase, String defaultValue)
	{
		this.index = index;
		this.fieldExpression = fieldExpression;
		this.operator = operator;
		this.joinOperator = joinOperator;
		this.nullable = nullable;
		this.ignoreCase = ignoreCase;
		this.defaultValue = defaultValue;
	}
	
	public Condition(int index, String embeddedProperty, String fieldExpression, Operator operator, JoinOperator joinOperator, boolean  nullable, boolean ignoreCase, String defaultValue)
	{
		this.index = index;
		this.embeddedProperty = embeddedProperty;
		this.operator = operator;
		this.fieldExpression = fieldExpression;
		this.joinOperator = joinOperator;
		this.nullable = nullable;
		this.ignoreCase = ignoreCase;
		this.defaultValue = defaultValue;
	}

	/**
	 * Converts the condition into bean expression that can be used to fetch
	 * condition value from method parameters
	 * 
	 * @return
	 */
	public String getConditionExpression()
	{
		StringBuilder builder = new StringBuilder("parameters[").append(index).append("]");

		if(embeddedProperty != null)
		{
			builder.append(".").append(embeddedProperty);
		}

		return builder.toString();
	}

	/**
	 * Adds required tables by this result field to the specified query.
	 * @param context Context to be used
	 * @param query Query to which tables should be added
	 */
	public void addRequiredTables(QueryBuilderContext context, IConditionalQuery query)
	{
		//if the result is going to be part of intermediate query
			// dont add its dependency tables to the query
		if(tableDataSource instanceof SubqueryBuilder)
		{
			if(context.currentQueryBuilder != tableDataSource)
			{
				return;
			}
		}
		
		table.addRequiredTables(context, query);
	}
}
