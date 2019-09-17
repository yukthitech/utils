package com.yukthitech.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class ToolbarButton extends JButton
{
	private static final long serialVersionUID = 1L;

	private static final Border ETCHED_BORDER = new EtchedBorder();

	private MouseListener borderHighlighter = new MouseAdapter()
	{
		public void mouseEntered(java.awt.event.MouseEvent e) 
		{
			ToolbarButton.super.setBorder(ETCHED_BORDER);
		};
		
		public void mouseExited(java.awt.event.MouseEvent e) 
		{
			ToolbarButton.super.setBorder(null);
		};
	};
	
	public ToolbarButton()
	{
		super.addMouseListener(borderHighlighter);
		super.setBorder(null);
		super.setContentAreaFilled(false);
	}
}
