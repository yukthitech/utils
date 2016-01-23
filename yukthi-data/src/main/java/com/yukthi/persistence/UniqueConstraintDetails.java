package com.yukthi.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UniqueConstraintDetails
{
	public static final String UNIQUE_CONSTRAINT_PREFIX = "UQ_";
	
	private EntityDetails entityDetails;
	private String name;
	private List<String> fields = new ArrayList<>();
	private String message;
	private boolean validate;

	public UniqueConstraintDetails(EntityDetails entityDetails, String name, String fields[], String message, boolean validate)
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new NullPointerException("Name can not be null or empty");
		}
		
		if(fields == null || fields.length == 0)
		{
			throw new NullPointerException("Fields can not be null or empty");
		}

		this.entityDetails = entityDetails;
		this.name = name;
		this.message = (message == null || message.trim().length() == 0) ? null : message.trim();
		this.validate = validate;
		
		this.fields.addAll(Arrays.asList(fields));
	}
	
	/**
	 * @return the {@link #entityDetails entityDetails}
	 */
	public EntityDetails getEntityDetails()
	{
		return entityDetails;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getConstraintName()
	{
		return  UNIQUE_CONSTRAINT_PREFIX + entityDetails.getEntityType().getSimpleName().toUpperCase() + "_" + name.toUpperCase();
	}
	
	public String getFieldsString()
	{
		return fields.toString();
	}

	public List<String> getFields()
	{
		return Collections.unmodifiableList(fields);
	}
	
	public boolean hasField(String field)
	{
		return fields.contains(field);
	}

	public String getMessage()
	{
		return message;
	}

	void setMessage(String message)
	{
		this.message = message;
	}

	public boolean isValidate()
	{
		return validate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Fields: ").append(fields);

		builder.append("]");
		return builder.toString();
	}
}
