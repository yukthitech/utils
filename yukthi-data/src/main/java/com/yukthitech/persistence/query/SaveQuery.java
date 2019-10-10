package com.yukthitech.persistence.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GenerationType;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;

public class SaveQuery extends Query
{
	private List<ColumnParam> columns;

	public SaveQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
	}

	/** 
	 * Adds value to {@link #columns Columns}
	 *
	 * @param column column to be added
	 */
	public void addColumn(ColumnParam column)
	{
		if(columns == null)
		{
			columns = new ArrayList<ColumnParam>();
		}

		columns.add(column);
	}

	public List<ColumnParam> getColumns()
	{
		return columns;
	}
	
	/**
	 * Checks if id field of current table is having sequence based id field.
	 *
	 * @return true, if is sequence id field
	 */
	public boolean isSequenceIdField()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return false;
		}
		
		return (idField.getGenerationType() == GenerationType.SEQUENCE);  
	}
	
	public String getIdFieldName()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return null;
		}

		return idField.getName();
	}
	
	/**
	 * Returns the sequence name used by id field of this table.
	 * @return
	 */
	public String getIdSequence()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return null;
		}
		
		String seqName = idField.getSequenceName();
		seqName = StringUtils.isBlank(seqName)? null : seqName.trim();
		
		return seqName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Columns: ").append(columns);

		builder.append("]");
		return builder.toString();
	}
}
