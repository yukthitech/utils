package com.yukthitech.autox.ide.layout;

import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class ToolbarSeparator extends JSeparator
{
	private static final long serialVersionUID = 1L;

	public ToolbarSeparator()
	{
		super(JSeparator.VERTICAL);
		super.setBorder(new EmptyBorder(4, 10, 4, 10));
	}
}
