package com.yukthitech.autox.expr;

import com.yukthitech.persistence.UnsupportedOperationException;

/**
 * Represents a path that can be used to get or set value.
 * @author akiran
 */
public interface IPropertyPath
{
	/**
	 * Fetches the value of the current path.
	 * @return value of the path
	 */
	public Object getValue() throws Exception;
	
	/**
	 * Sets the value of the current path.
	 * @param value value to set.
	 */
	public default void setValue(Object value) throws Exception
	{
		throw new UnsupportedOperationException("Write is not supported with this expression type");
	}

	public default void removeValue() throws Exception
	{
		throw new UnsupportedOperationException("Remove is not supported with this expression type");
	}
}
