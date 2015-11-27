package com.yukthi.persistence.query;

import java.util.ArrayList;
import java.util.List;

import com.yukthi.persistence.EntityDetails;

public class UpdateQuery extends AbstractConditionalQuery
{
	private List<UpdateColumnParam> columns;

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

	public List<UpdateColumnParam> getColumns()
	{
		return columns;
	}

}
