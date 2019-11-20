package com.yukthitech.autox.ide.xmlfile;

/**
 * Represents a function call.
 * @author akiran
 */
public class FunctionCall
{
	/**
	 * Name of function being invoked.
	 */
	private String name;

	/**
	 * Instantiates a new function call.
	 *
	 * @param name the name
	 */
	public FunctionCall(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the name of function being invoked.
	 *
	 * @return the name of function being invoked
	 */
	public String getName()
	{
		return name;
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

		builder.append("]");
		return builder.toString();
	}

}
