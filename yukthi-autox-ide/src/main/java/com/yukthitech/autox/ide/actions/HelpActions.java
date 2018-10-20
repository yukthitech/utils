package com.yukthitech.autox.ide.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.help.HelpPanel;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class HelpActions
{
	@Autowired
	private HelpPanel helpPanel;

	@Action
	public void help()
	{
		helpPanel.activatePanel();
	}
}
