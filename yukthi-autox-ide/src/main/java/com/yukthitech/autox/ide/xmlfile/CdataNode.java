package com.yukthitech.autox.ide.xmlfile;

/**
 * Represent cdata text content of the node.
 * @author akiran
 */
public class CdataNode implements INode
{
	/**
	 * Content of the text node.
	 */
	private String content;
	
	/**
	 * Start location of cdata section.
	 */
	private LocationRange location;

	/**
	 * Instantiates a new cdata node.
	 *
	 * @param content the content
	 * @param location the location
	 */
	public CdataNode(String content, LocationRange location)
	{
		this.content = content;
		this.location = location;
	}

	/**
	 * Gets the content of the text node.
	 *
	 * @return the content of the text node
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Gets the start location of cdata section.
	 *
	 * @return the start location of cdata section
	 */
	public LocationRange getLocation()
	{
		return location;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.ide.xmlfile.INode#toText(java.lang.String, java.lang.StringBuilder)
	 */
	@Override
	public void toText(String indent, StringBuilder builder)
	{
		builder.append("<![CDATA[");
		String lines[] = content.split("\\n");
		
		for(String line : lines)
		{
			builder.append("\n").append(indent).append(line.trim());
		}
		
		builder.append("\n").append(indent).append("]]>");
	}
}
