package com.yukthitech.autox.ide.editor;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class GutterPopup extends JPopupMenu
{
	private static final long serialVersionUID = 1L;

	private JMenuItem breakPointToggleItem = new JMenuItem("Toggle Breakpoint");
	private JMenuItem breakPointPropItem = new JMenuItem("Breakpoint Properties");
	
	private FileEditor activeEditor;
	
	private Point mousePoint;
	
	public GutterPopup()
	{
		add(breakPointToggleItem);
		add(breakPointPropItem);
		
		breakPointToggleItem.addActionListener(this::takeDefaultAction);
	}
	
	public void setActiveEditor(FileEditor activeEditor, Point mousePoint)
	{
		this.activeEditor = activeEditor;
		this.mousePoint = mousePoint;
	}
	
	private void takeDefaultAction(ActionEvent e)
	{
		activeEditor.getIconManager().toggleBreakPoint(mousePoint);
	}
}
