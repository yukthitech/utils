package com.yukthitech.swing;

import javax.swing.JButton;

public class IconButton extends JButton
{
	private static final long serialVersionUID = 1L;

	public IconButton()
	{
		BrdrHighligherMouseListener.applyTo(this);
		super.setContentAreaFilled(false);
		super.setFocusable(false);
		super.setBackground(null);
	}
}
