package com.yukthitech.transform.template;

/**
 * Wrapper for JSON/XML literal values (numbers, booleans, plain maps/lists, etc.) that appear
 * in the template tree but do not require compilation.
 */
public class TemplateLeaf extends TransformTemplate.TransformElement
{
	private static final long serialVersionUID = 1L;

	private final Object value;

	public TemplateLeaf(Location location, Object value)
	{
		super(location);
		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public void compile(TemplateCompileContext context)
	{
		// no compiled artifacts
	}

	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
}
