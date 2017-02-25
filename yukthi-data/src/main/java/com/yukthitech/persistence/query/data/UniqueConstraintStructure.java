package com.yukthitech.persistence.query.data;

import java.util.Map;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.UniqueConstraintDetails;

public class UniqueConstraintStructure
{
	private String name;
	private String columns[];
	
	public UniqueConstraintStructure(EntityDetails entityDetails, UniqueConstraintDetails constraint, Map<String, String> fieldMapping)
	{
		this.name = constraint.getConstraintName();
		
		//FieldDetails fieldDetails = null;
		columns = new String[constraint.getFields().size()];
		
		int idx = 0;
		
		for(String field: constraint.getFields())
		{
			//fieldDetails = entityDetails.getFieldDetailsByField(field);
			columns[idx] = fieldMapping.get(field);
			
			idx++;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String[] getColumns()
	{
		return columns;
	}

	public void setColumns(String[] columns)
	{
		this.columns = columns;
	}

}
