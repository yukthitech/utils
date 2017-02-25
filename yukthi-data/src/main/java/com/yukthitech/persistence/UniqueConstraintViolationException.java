package com.yukthitech.persistence;

/**
 * Thrown when unique constraint on entity is violated
 * @author akiran
 */
public class UniqueConstraintViolationException extends PersistenceException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Entity type on which this constraint is defined
	 */
	private Class<?> entityType;
	
	/**
	 * Fields which are involved in unique constraint
	 */
	private String fields[];
	
	/**
	 * Name of the unique constraint name
	 */
	private String constraintName;

	/**
	 * Instantiates a new unique constraint violation exception.
	 *
	 * @param entityType the entity type
	 * @param fields the fields
	 * @param constraintName the constraint name
	 * @param message the message
	 * @param cause the cause
	 */
	public UniqueConstraintViolationException(Class<?> entityType, String fields[], String constraintName, String message, Throwable cause)
	{
		super(message, cause);
		
		this.entityType = entityType;
		this.fields = fields;
		this.constraintName = constraintName;
	}

	/**
	 * Instantiates a new unique constraint violation exception.
	 *
	 * @param entityType the entity type
	 * @param fields the fields
	 * @param constraintName the constraint name
	 * @param message the message
	 */
	public UniqueConstraintViolationException(Class<?> entityType, String fields[], String constraintName, String message)
	{
		this(entityType, fields, constraintName, message, null);
	}

	/**
	 * Gets the entity type on which this constraint is defined.
	 *
	 * @return the entity type on which this constraint is defined
	 */
	public Class<?> getEntityType()
	{
		return entityType;
	}

	/**
	 * Gets the fields which are involved in unique constraint.
	 *
	 * @return the fields which are involved in unique constraint
	 */
	public String[] getFields()
	{
		return fields;
	}

	/**
	 * Gets the name of the unique constraint name.
	 *
	 * @return the name of the unique constraint name
	 */
	public String getConstraintName()
	{
		return constraintName;
	}
}
