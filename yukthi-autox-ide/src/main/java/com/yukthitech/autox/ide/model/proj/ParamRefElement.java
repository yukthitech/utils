package com.yukthitech.autox.ide.model.proj;

import java.io.File;

import com.yukthitech.autox.ide.FileParseCollector;

/**
 * Element where attribute is referred.
 * @author akiran
 */
public class ParamRefElement extends ReferenceElement
{
	/**
	 * Name of parameter being referred.
	 */
	private String name;

	public ParamRefElement(File file, int lineNo, int pos, int end, String name)
	{
		super(file, lineNo, pos, end);
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	@Override
	public void compile(FileParseCollector collector)
	{
	}
}
