package com.yukthitech.autox.ide.model.proj;

import java.io.File;

public abstract class ReferenceElement extends CodeElement
{
	protected int lineNo;
	
	protected int end;
	
	public ReferenceElement(File file, int lineNo, int position, int end)
	{
		super(file, position);
		this.lineNo = lineNo;
		this.end = end;
	}

	public int getEnd()
	{
		return end;
	}
}
