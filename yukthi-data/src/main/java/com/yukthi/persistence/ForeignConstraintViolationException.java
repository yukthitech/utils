package com.yukthi.persistence;

/**
 * The Class ForeignConstraintViolationException.
 */
public class ForeignConstraintViolationException extends PersistenceException
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Entity type on which this constraint is defined
	 */
	private Class<?> entityType;

	/**
	 * Name of the unique constraint name
	 */
	private String constraintName;

	/**
	 * Instantiates a new foreign constraint violation exception.
	 *
	 * @param entityType the entity type
	 * @param constraintName the constraint name
	 * @param message the message
	 * @param cause the cause
	 */
	public ForeignConstraintViolationException(Class<?> entityType, String constraintName, String message, Throwable cause)
	{
		super(message, cause);
		this.constraintName = constraintName;
	}

	/**
	 * Instantiates a new foreign constraint violation exception.
	 *
	 * @param entityType the entity type
	 * @param constraintName the constraint name
	 * @param message the message
	 */
	public ForeignConstraintViolationException(Class<?> entityType, String constraintName, String message)
	{
		this(entityType, constraintName, message, null);
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
	 * Gets the name of the unique constraint name.
	 *
	 * @return the name of the unique constraint name
	 */
	public String getConstraintName()
	{
		return constraintName;
	}

	
}
