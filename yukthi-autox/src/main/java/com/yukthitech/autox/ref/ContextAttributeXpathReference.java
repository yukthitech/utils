package com.yukthitech.autox.ref;

import java.io.Serializable;

import org.apache.commons.jxpath.JXPathContext;

import com.yukthitech.autox.AutomationContext;

/**
 * Represents a reference to context attribute using xpath.
 * @author akiran
 */
public class ContextAttributeXpathReference implements Serializable, IReference
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Path of the attribute being referenced.
	 */
	private String path;

	public ContextAttributeXpathReference(String name)
	{
		this.path = name;
	}
	
	@Override
	public Object getValue(AutomationContext context)
	{
		return JXPathContext.newContext(context.getAttr()).getValue(path);
	}
}
