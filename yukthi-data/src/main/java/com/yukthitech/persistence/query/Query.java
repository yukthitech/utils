package com.yukthitech.persistence.query;

import java.util.Iterator;
import java.util.List;

import com.yukthitech.persistence.EntityDetails;

public abstract class Query
{
	/**
	 * Basic entity details which provides main table.
	 */
	protected EntityDetails entityDetails;
	
	/**
	 * Main table code to be used in query.
	 */
	protected String mainTableCode = "T0";
	
	public Query(EntityDetails entityDetails)
	{
		this.entityDetails = entityDetails;
	}

	/**
	 * Gets the basic entity details which provides main table.
	 *
	 * @return the basic entity details which provides main table
	 */
	public EntityDetails getEntityDetails()
	{
		return entityDetails;
	}
	
	/**
	 * Gets the main table code to be used in query.
	 *
	 * @return the main table code to be used in query
	 */
	public String getMainTableCode()
	{
		return mainTableCode;
	}
	
	/**
	 * Sets the main table code to be used in query.
	 *
	 * @param mainTableCode the new main table code to be used in query
	 */
	public void setMainTableCode(String mainTableCode)
	{
		this.mainTableCode = mainTableCode;
	}
	
	/**
	 * Gets the main table name from basic entity details.
	 * @return Main table name
	 */
	public String getTableName()
	{
		return entityDetails.getTableName();
	}

	public static void toString(List<QueryCondition> conditions, StringBuilder builder)
	{
		if(conditions == null || conditions.isEmpty())
		{
			builder.append("{}");
			return;
		}
		
		Iterator<QueryCondition> it = conditions.iterator();
		
		builder.append("{");
		
		while(it.hasNext())
		{
			builder.append(it.next());
			
			if(it.hasNext())
			{
				builder.append(" AND ");
			}
		}
		
		builder.append("}");
	}
}
