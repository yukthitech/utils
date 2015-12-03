package com.yukthi.persistence.query;

import java.util.ArrayList;
import java.util.List;

import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.Operator;

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
		this.value = value;
		this.joinOperator = joinOperator;
		this.ignoreCase = ignoreCase;
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
		builder.append(tableCode).append(".").append(column).append(" ");
		builder.append(operator).append(" ").append(value);

		return builder.toString();
	}
}
