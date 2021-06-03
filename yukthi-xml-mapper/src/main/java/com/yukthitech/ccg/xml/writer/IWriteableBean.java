package com.yukthitech.ccg.xml.writer;

/**
 * Expected to be implemented by beans which can self convert themselves into DOM element.
 * @author akranthikiran
 */
public interface IWriteableBean
{
	/**
	 * Expectes the implementation to populate attributes and subnodes of current-element specified by context.
	 * @param context
	 */
	public void writeTo(XmlWriterContext context);
}
