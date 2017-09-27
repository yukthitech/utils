package com.yukthitech.autox.ref;

import java.io.Serializable;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.AutomationContext;

/**
 * Represents a reference to context attribute.
 * @author akiran
 */
public class ContextAttributeReference implements Serializable, IReference
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the attribute being referenced.
	 */
	private String name;

	public ContextAttributeReference(String name)
	{
		this.name = name;
	}
	
	@Override
	public Object getValue(AutomationContext context)
	{
		try
		{
			return PropertyUtils.getProperty(context.getAttr(), name);
		} catch(NoSuchMethodException ex)
		{
			return null;
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while fetching context attribute: " + name, ex);
		}
	}
}
