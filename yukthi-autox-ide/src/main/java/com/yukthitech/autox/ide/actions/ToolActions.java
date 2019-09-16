package com.yukthitech.autox.ide.actions;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.dialog.XpathSandboxDialog;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class ToolActions
{
	/**
	 * Used to display about autox info.
	 */
	private XpathSandboxDialog xpathSandboxDialog = new XpathSandboxDialog();
	
	@Action
	public void displayXpathSandbox()
	{
		IdeUtils.maximize(xpathSandboxDialog, 50);
		IdeUtils.centerOnScreen(xpathSandboxDialog);
		xpathSandboxDialog.display();
	}
}
