package com.yukthitech.autox.ide.editor;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GutterPopup extends JPopupMenu
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JMenuItem toggle= new JMenuItem("Toggle Breakpoint");
	JMenuItem bookmark= new JMenuItem("Add Bookmark");
	public GutterPopup()
	{
		// TODO Auto-generated constructor stub
		add(toggle);
		add(bookmark);
	}
}
