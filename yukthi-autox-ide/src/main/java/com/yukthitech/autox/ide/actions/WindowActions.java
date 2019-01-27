package com.yukthitech.autox.ide.actions;

import java.awt.Component;
import java.awt.Window;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.MaximizableTabbedPane;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class WindowActions
{
	@Action
	public void flipMaximization()
	{
		Window window = IdeUtils.getCurrentWindow();
		Component focusOwner = window.getFocusOwner();
		
		if(focusOwner == null)
		{
			return;
		}
		
		Component curComp = focusOwner;
		
		while(curComp != null)
		{
			if(curComp instanceof MaximizableTabbedPane)
			{
				((MaximizableTabbedPane) curComp).flipMaximization();
				break;
			}
			
			curComp = curComp.getParent();
		}

		focusOwner.requestFocus();
	}
}
