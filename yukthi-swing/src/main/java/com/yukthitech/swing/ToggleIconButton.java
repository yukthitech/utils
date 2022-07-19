package com.yukthitech.swing;

import javax.swing.JToggleButton;

public class ToggleIconButton extends JToggleButton
{
	private static final long serialVersionUID = 1L;

	public ToggleIconButton()
	{
		BrdrHighligherMouseListener.applyTo(this);
		super.setFocusable(false);
	}
}
