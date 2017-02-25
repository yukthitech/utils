package com.yukthitech.ccg.xml;

/**
 * Beans implementing indicate bean parser that this
 * bean can accept non-standard attributes (attributes which does not represent bean property).
 * @author akiran
 */
public interface IDynamicAttributeAcceptor
{
	/**
	 * Called by parser when a non-standard attribute is found.
	 * @param attrName Attribute name
	 * @param value Value of attribute
	 */
	public void set(String attrName, String value);
}
