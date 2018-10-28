package com.yukthitech.autox.ide.projpropdialog;

public class NodeData
{

	protected String label;
	protected boolean checked;

	public NodeData(String quest)
	{
		label = quest;
	}

	public String getLable()
	{
		return label;
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
		return label + " = " + checked;
	}
}