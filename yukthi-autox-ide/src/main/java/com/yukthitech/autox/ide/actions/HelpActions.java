package com.yukthitech.autox.ide.actions;

import java.awt.Component;
import java.awt.Window;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.help.HelpPanel;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class HelpActions
{
	@Autowired
	private HelpPanel helpPanel;
	
	private FileEditor getActiveFileEditor()
	{
		Window activeWindow = IdeUtils.getCurrentWindow();
		
		if(activeWindow == null)
		{
			return null;
		}
		
		Component activeComp = activeWindow.getFocusOwner();
		
		if(activeComp == null)
		{
			return null;
		}
		
		Component curComp = activeComp;
		
		while(curComp != null)
		{
			if(curComp instanceof FileEditor)
			{
				return (FileEditor) curComp;
			}
			
			curComp = curComp.getParent();
		}
		
		return null;
	}

	@Action
	public void help()
	{
		helpPanel.activatePanel(null);
	}

	@Action
	public void contextHelp()
	{
		FileEditor fileEditor = getActiveFileEditor();
		String currentWord = fileEditor.getCursorWord();
		
		helpPanel.activatePanel(currentWord);
	}
}
