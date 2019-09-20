package com.yukthitech.autox.ide.services;

import java.awt.Font;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.dialog.FontDialog;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.IdeSettings;
import com.yukthitech.autox.ide.model.IdeState;

@ActionHolder
public class IdeSettingsManager
{
	@Autowired
	private IdeStateManager ideStateManager;
	
	@Autowired
	private IdeEventManager ideEventManager;
	
	@Autowired
	private FontDialog fontDialog;
	
	@Action
	public void changeEditorFont()
	{
		IdeState ideState = ideStateManager.getState();
		IdeSettings ideSettings = ideState.getIdeSettings();
		
		Font newFont = fontDialog.display(ideSettings.getEditorFont());
		
		if(newFont == null)
		{
			return;
		}

		ideSettings.setEditorFont(newFont);
		ideEventManager.processEvent(new IdeSettingChangedEvent(ideSettings));
		
		ideStateManager.saveState(ideState);
	}
}
