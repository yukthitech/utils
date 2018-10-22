package com.yukthitech.autox.ide.projpropdialog;

public class NodeData
{

	protected final String value;
	protected boolean checked;

	public NodeData(String quest)
	{
		value = quest;
	}

	public String getValue()
	{
		return value;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public String toString()
	{
		return value + " = " + checked;
	}
}