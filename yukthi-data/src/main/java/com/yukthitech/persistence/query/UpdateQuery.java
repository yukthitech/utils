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
