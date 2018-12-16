package com.yukthitech.autox.test.ui.common;

import com.yukthitech.autox.Param;

/**
 * Represents field options.
 * @author akiran
 */
public class FieldOption
{
	/**
	 * Option value.
	 */
	@Param(description = "Value of the option")
	private String value;
	
	/**
	 * Option label.
	 */
	@Param(description = "Label of the option")
	private String label;
	
	/**
	 * Instantiates a new field option.
	 */
	public FieldOption()
	{}
	
	/**
	 * Instantiates a new field option.
	 *
	 * @param value the value
	 * @param label the label
	 */
	public FieldOption(String value, String label)
	{
		this.value = value;
		this.label = label;
	}

	/**
	 * Gets the option value.
	 *
	 * @return the option value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the option value.
	 *
	 * @param value the new option value
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the option label.
	 *
	 * @return the option label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the option label.
	 *
	 * @param label the new option label
	 */
	public void setLabel(String label)
	{
		this.label = label;
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

		if(!(obj instanceof FieldOption))
		{
			return false;
		}

		FieldOption other = (FieldOption) obj;
		return value.equals(other.value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return value.hashCode();
	}
}
