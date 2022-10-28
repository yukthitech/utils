package com.yukthitech.swing.table;

/**
 * Details about a table column.
 * @author akranthikiran
 */
public class SimpleTableColumn
{
	/**
	 * Heading.
	 */
	private String heading;
	
	/**
	 * Type of data in this column.
	 */
	private Class<?> type;
	
	/**
	 * Flag indicating if this column is editable or not.
	 */
	private boolean editable;

	/**
	 * Instantiates a new simple table column.
	 *
	 * @param heading heading.
	 * @param type type of data in this column.
	 * @param editable flag indicating if this column is editable or not.
	 */
	public SimpleTableColumn(String heading, Class<?> type, boolean editable)
	{
		this.heading = heading;
		this.type = type;
		this.editable = editable;
	}

	/**
	 * Gets the heading.
	 *
	 * @return the heading
	 */
	public String getHeading()
	{
		return heading;
	}

	/**
	 * Sets the heading.
	 *
	 * @param heading the new heading
	 */
	public void setHeading(String heading)
	{
		this.heading = heading;
	}

	/**
	 * Gets the type of data in this column.
	 *
	 * @return the type of data in this column
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 * Sets the type of data in this column.
	 *
	 * @param type the new type of data in this column
	 */
	public void setType(Class<?> type)
	{
		this.type = type;
	}

	/**
	 * Checks if is flag indicating if this column is editable or not.
	 *
	 * @return the flag indicating if this column is editable or not
	 */
	public boolean isEditable()
	{
		return editable;
	}

	/**
	 * Sets the flag indicating if this column is editable or not.
	 *
	 * @param editable the new flag indicating if this column is editable or not
	 */
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}
}
