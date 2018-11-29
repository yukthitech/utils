package com.yukthitech.autox.ide.json;

public class AbstractJsonElement implements IJsonElement
{
	private int startLineNumber;
	
	private int endLineNumber;
	
	private int startColumnNumber;
	
	private int endColumnNumber;

	@Override
	public int getStartLineNumber()
	{
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber)
	{
		this.startLineNumber = startLineNumber;
	}

	@Override
	public int getEndLineNumber()
	{
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber)
	{
		this.endLineNumber = endLineNumber;
	}

	@Override
	public int getStartColumnNumber()
	{
		return startColumnNumber;
	}

	public void setStartColumnNumber(int startColumnNumber)
	{
		this.startColumnNumber = startColumnNumber;
	}

	@Override
	public int getEndColumnNumber()
	{
		return endColumnNumber;
	}

	public void setEndColumnNumber(int endColumnNumber)
	{
		this.endColumnNumber = endColumnNumber;
	}
}
