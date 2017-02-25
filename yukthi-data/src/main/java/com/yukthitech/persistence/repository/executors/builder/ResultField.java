package com.yukthitech.persistence.repository.executors.builder;

import java.lang.reflect.Type;

import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.query.IConditionalQuery;
import com.yukthitech.persistence.repository.annotations.OrderByType;

/**
 * Result field details that is part current query
 * 
 * @author akiran
 */
public class ResultField
{
	/**
	 * Entity field expression mapping.
	 */
	String entityFieldExpression;

	/**
	 * Property of the result bean to which resultant value should be populated
	 */
	String property;
	
	/**
	 * Type of result field
	 */
	Class<?> fieldType;
	
	/**
	 * Generic Type of result field
	 */
	Type genericFieldType;

	/**
	 * Ordering to be applied on this field if any.
	 */
	OrderByType orderType;
	
	/**
	 * Short code for result field
	 */
	String code;

	/**
	 * Table from which value can be fetched
	 */
	TableInfo table;

	/**
	 * Field details (with column) from which value can be fetched
	 */
	FieldDetails fieldDetails;
	
	/**
	 * Table data source from which this result can be obtained.
	 */
	ITableDataSource tableDataSource;

	public ResultField(String entityFieldExpression, String property, Class<?> fieldType, Type genericFieldType, OrderByType orderType)
	{
		this.entityFieldExpression = entityFieldExpression;
		this.property = property;
		this.fieldType = fieldType;
		this.genericFieldType = genericFieldType;
		this.orderType = orderType;
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
