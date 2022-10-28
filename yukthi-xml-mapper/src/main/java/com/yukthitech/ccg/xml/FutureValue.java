package com.yukthitech.ccg.xml;

/**
 * A value whose value will be decided in future (mostly at end of node).
 * @author akranthikiran
 */
public class FutureValue implements IHybridTextBean
{
	private String value = "";
	
	private Object finalValue;
	
	@Override
	public void addText(String text)
	{
		value += text;
	}
	
	public void setValue(Object value)
	{
		this.finalValue = value;
	}
	
	public Object getValue()
	{
		if(finalValue != null)
		{
			return finalValue;
		}
		
		return value;
	}
}
