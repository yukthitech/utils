package com.yukthitech.swing;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class EscapableDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	public EscapableDialog()
	{}

	@Override
	protected JRootPane createRootPane()
	{
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		
		rootPane.registerKeyboardAction(e -> 
		{
			hideDialog();
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		return rootPane;
	}
	
	protected void hideDialog()
	{
		setVisible(false);
	}
}
