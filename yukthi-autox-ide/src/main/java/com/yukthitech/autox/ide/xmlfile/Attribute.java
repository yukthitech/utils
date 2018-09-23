package com.yukthitech.autox.ide.xmlfile;

public class Attribute
{
	private String namespace;
	
	private String name;
	
	private String value;
	
	public Attribute()
	{}
	
	public Attribute(String namespace, String name, String value)
	{
		this.namespace = namespace;
		this.name = name;
		this.value = value;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Value: ").append(value);

		builder.append("]");
		return builder.toString();
	}

}
