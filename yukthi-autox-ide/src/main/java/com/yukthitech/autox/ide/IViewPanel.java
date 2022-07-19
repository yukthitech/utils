package com.yukthitech.autox.ide;

import javax.swing.JTabbedPane;

/**
 * Represents view of the ide.
 * @author akiran
 */
public interface IViewPanel
{
	/**
	 * Sets the parent which has current view panel.
	 * @param parentTabPane
	 */
	public void setParent(JTabbedPane parentTabPane);
}
