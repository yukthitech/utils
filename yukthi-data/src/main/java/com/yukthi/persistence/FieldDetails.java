package com.yukthi.persistence;

import java.lang.reflect.Field;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GenerationType;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.NotUpdateable;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.exceptions.InvalidStateException;

/**
 * The Class FieldDetails.
 */
public class FieldDetails
{
	/** The Constant FLAG_ID. */
	public static final int FLAG_ID = 1;
	
	/** The Constant FLAG_AUTO_FETCH. */
	public static final int FLAG_AUTO_FETCH = 4;
	
	/** The Constant SUPPORTED_VERSION_FIELD_TYPES. */
	private static final Set<Class<?>> SUPPORTED_VERSION_FIELD_TYPES = CommonUtils.toSet(
			int.class, Integer.class,
			long.class, Long.class
	);
	
	/**
	 * Name of the field.
	 */
	private String name;
	
	/** The field. */
	private Field field;
	
	/** The db data type. */
	private DataType dbDataType;
	
	/** The flags. */
	private int flags;
	
	/** The generation type. */
	private GenerationType generationType;
	
	/** The sequence name. */
	private String sequenceName;
	
	/**
	 * Name of the db column
	 */
	private String dbColumnName;
	
	/**
	 * Version field used for optimistic updates
	 */
	private boolean versionField;
	
	/**
	 * Indicates if this field is nullable or not
	 */
	private boolean nullable;
	
	/**
	 * field is updateable or not
	 */
	private boolean updateable = true;
	
	/**
	 * Foreign constraint on this field, if specified
	 */
	private ForeignConstraintDetails foreignConstraintDetails;
	
	/**
	 * Length of the field. Applicable only for string fields.
	 */
	private int length = 255;
	
	/**
	 * Instantiates a new field details.
	 *
	 * @param details the details
	 */
	private FieldDetails(FieldDetails details)
	{
		this.field = details.field;
		this.dbColumnName = details.dbColumnName;
		this.dbDataType = details.dbDataType;
		this.dbColumnName = details.dbColumnName;
	}
	
	/**
	 * Instantiates a new field details.
	 *
	 * @param field the field
	 * @param dbDataType the db data type
	 * @param isVersionField the is version field
	 * @param nullable the nullable
	 */
	public FieldDetails(Field field, DataType dbDataType, boolean isVersionField, boolean nullable)
	{
		if(field == null)
		{
			throw new NullPointerException("Field can not be null");
		}

		/*
		 * Column validation is commented as column is not mandatory for non-owned relation fields
		if(column == null || column.trim().length() == 0)
		{
			throw new NullPointerException("Column can not be null or empty");
		}
		*/

		if(isVersionField && !SUPPORTED_VERSION_FIELD_TYPES.contains(field.getType()))
		{
			throw new InvalidStateException("Field '{}' with unsupported data type '{}' is marked as version field.", field.getName(), field.getType().getName());
		}
		
		this.field = field;
		this.dbDataType = dbDataType;
		this.nullable = nullable;
		this.updateable = (field.getAnnotation(NotUpdateable.class) == null);

		if(!field.isAccessible())
		{
			field.setAccessible(true);
		}
		
		this.versionField = isVersionField;
		populateInternalDetails();
	}

	/**
	 * Instantiates a new field details.
	 *
	 * @param field the field
	 * @param dbDataType the db data type
	 * @param idField the id field
	 * @param generationType the generation type
	 * @param autoFetch the auto fetch
	 * @param sequenceName the sequence name
	 * @param nullable the nullable
	 */
	public FieldDetails(Field field, DataType dbDataType, boolean idField, GenerationType generationType, boolean autoFetch, String sequenceName, boolean nullable)
	{
		this(field, dbDataType, false, nullable);
		
		if(generationType == GenerationType.SEQUENCE && (sequenceName == null || sequenceName.trim().length() == 0))
		{
			throw new NullPointerException("For sequence generation type, sequence name is mandatory");
		}
		
		this.flags = idField ? (this.flags | FLAG_ID) : flags;
		this.flags = autoFetch ? (this.flags | FLAG_AUTO_FETCH) : flags;
		
		this.generationType = generationType;
		this.sequenceName = sequenceName;
		
		populateInternalDetails();
	}
	
	/**
	 * Populate internal details.
	 */
	private void populateInternalDetails()
	{
		this.name = field.getName();
		
		Column column = field.getAnnotation(Column.class);
		this.length = (column != null) ? column.length() : 255;

		if(column == null || column.name().trim().length() == 0)
		{
			dbColumnName = this.name;
			
			dbColumnName = dbColumnName.replace("_", "");
			dbColumnName = dbColumnName.replaceAll("([A-Z])", "_$1");
			dbColumnName = dbColumnName.toUpperCase();
		}
		else
		{
			dbColumnName = column.name().trim();
		}
	}
	
	/**
	 * Sets the name of the field.
	 *
	 * @param name the new name of the field
	 */
	void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the length of the field. Applicable only for string fields.
	 *
	 * @return the length of the field
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * Sets the length of the field. Applicable only for string fields.
	 *
	 * @param length the new length of the field
	 */
	void setLength(int length)
	{
		this.length = length;
	}

	/**
	 * Gets the field.
	 *
	 * @return the field
	 */
	public Field getField()
	{
		return field;
	}
	
	/**
	 * Gets the db data type.
	 *
	 * @return the db data type
	 */
	public DataType getDbDataType()
	{
		return dbDataType;
	}

	/**
	 * Checks if is id field.
	 *
	 * @return true, if is id field
	 */
	public boolean isIdField()
	{
		return ((flags & FLAG_ID) == FLAG_ID);
	}
	
	/**
	 * Checks if is auto fetch.
	 *
	 * @return true, if is auto fetch
	 */
	public boolean isAutoFetch()
	{
		return ((flags & FLAG_AUTO_FETCH) == FLAG_AUTO_FETCH);
	}

	/**
	 * Gets the generation type.
	 *
	 * @return the generation type
	 */
	public GenerationType getGenerationType()
	{
		return generationType;
	}
	
	/**
	 * Gets the sequence name.
	 *
	 * @return the sequence name
	 */
	public String getSequenceName()
	{
		return sequenceName;
	}
	
	/**
	 * Gets the value.
	 *
	 * @param bean the bean
	 * @return the value
	 */
	public Object getValue(Object bean)
	{
		try
		{
			return field.get(bean);
		}catch(Exception ex)
		{
			throw new IllegalStateException("Failed to fetch value from field: " + field.getName(), ex);
		}
	}

	/**
	 * Sets the value.
	 *
	 * @param bean the bean
	 * @param value the value
	 */
	public void setValue(Object bean, Object value)
	{
		try
		{
			field.set(bean, value);
		}catch(Exception ex)
		{
			throw new IllegalStateException("Failed to setting value from field: " + field.getName(), ex);
		}
	}

	/**
	 * Gets the name of the db column.
	 *
	 * @return the name of the db column
	 */
	public String getDbColumnName()
	{
		return dbColumnName;
	}

	/**
	 * Sets the name of the db column.
	 *
	 * @param dbColumnName the new name of the db column
	 */
	void setDbColumnName(String dbColumnName)
	{
		this.dbColumnName = dbColumnName;
	}

	/**
	 * Clone for audit.
	 *
	 * @return the field details
	 */
	public FieldDetails cloneForAudit()
	{
		return new FieldDetails(this);
	}
	
	/**
	 * Checks if is version field used for optimistic updates.
	 *
	 * @return the version field used for optimistic updates
	 */
	public boolean isVersionField()
	{
		return versionField;
	}
	

	/**
	 * @return the {@link #foreignConstraintDetails foreignConstraintDetails}
	 */
	public ForeignConstraintDetails getForeignConstraintDetails()
	{
		return foreignConstraintDetails;
	}

	/**
	 * @param foreignConstraintDetails the {@link #foreignConstraintDetails foreignConstraintDetails} to set
	 */
	public void setForeignConstraintDetails(ForeignConstraintDetails foreignConstraintDetails)
	{
		this.foreignConstraintDetails = foreignConstraintDetails;
	}
	
	/**
	 * Returns true, if this field indicates a relation to other entity
	 * @return
	 */
	public boolean isRelationField()
	{
		return (foreignConstraintDetails != null);
	}
	
	/**
	 * Returns true, if this field is relation field but relation is not owned by current entity
	 * @return
	 */
	public boolean isMappedRelationField()
	{
		return (foreignConstraintDetails != null && foreignConstraintDetails.isMappedRelation());
	}
	
	/**
	 * Returns true, if the relation is joined by external table
	 * @return
	 */
	public boolean isTableJoined()
	{
		return ( foreignConstraintDetails != null && (foreignConstraintDetails.getJoinTableDetails() != null) );
	}
	
	/**
	 * Returns true, if the current column is normal column or if this is relation field and foreign key column is part of current table
	 * @return
	 */
	public boolean isTableOwned()
	{
		//if this is normal field
		if(foreignConstraintDetails == null)
		{
			return true;
		}
		
		//return true, if this is not mapped relation and join table is not specified
		return (!foreignConstraintDetails.isMappedRelation() && (foreignConstraintDetails.getJoinTableDetails() == null));
	}
	
	/**
	 * Checks if is indicates if this field is nullable or not.
	 *
	 * @return the indicates if this field is nullable or not
	 */
	public boolean isNullable()
	{
		return nullable;
	}
	
	/**
	 * Checks if is field is updateable or not.
	 *
	 * @return the field is updateable or not
	 */
	public boolean isUpdateable()
	{
		return updateable;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof FieldDetails))
		{
			return false;
		}

		FieldDetails other = (FieldDetails) obj;
		return field.equals(other.field);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return field.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Field: ").append(field);
		builder.append(",").append("Column: ").append(dbColumnName);
		builder.append(",").append("ID Field: ").append(isIdField());

		builder.append("]");
		return builder.toString();
	}
}
