package com.yukthitech.persistence.query;

import javax.persistence.GenerationType;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;

/**
 * Drop query to drop underlying entity table 
 * @author akiran
 */
public class DropTableQuery extends Query
{
	private String tableName;
	
	public DropTableQuery(EntityDetails entityDetails)
	{
		super(entityDetails);
		
		this.tableName = entityDetails.getTableName();
	}
	
	public String getTableName()
	{
		return tableName;
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

	/**
	 * Checks if id field of current table is having auto id field.
	 *
	 * @return true, if is auto id field
	 */
	public boolean isAutoIdField()
	{
		FieldDetails idField = entityDetails.getIdField();
		
		if(idField == null)
		{
			return false;
		}
		
		return (idField.getGenerationType() == GenerationType.IDENTITY);  
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
		StringBuilder builder = new StringBuilder(getClass().getName());
		builder.append("{ ").append(getTableName()).append(" }");
		return builder.toString();
	}
}
