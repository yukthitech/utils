package com.yukthi.persistence.repository.search;

import java.util.ArrayList;
import java.util.List;

import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.Operator;

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
		this.field = field;
		this.operator = operator;
		this.value = value;
		this.nullable = nullable;
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
		this.field = field;
		this.operator = operator;
		this.value = value;
		this.joinOperator = joinOperator;
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
		this.value = value;
		this.joinOperator = joinOperator;
		this.nullable = nullable;
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
