package com.yukthitech.persistence.query;

import com.yukthitech.persistence.EntityDetails;

/**
 * Drop query to drop underlying entity table 
 * @author akiran
 */
public class DropTableQuery extends Query
{
	private String tableName;
	
	public DropTableQuery(EntityDetails entityDetails, String mainTableCode)
	{
		super(entityDetails);
		
		this.tableName = entityDetails.getTableName();
	}
	
	public String getTableName()
	{
		return tableName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getName());
		builder.append("{ ").append(getTableName()).append(" }");
		return builder.toString();
	}
}
