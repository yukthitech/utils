package com.yukthitech.autox.ide.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JToolBar;

/**
 * Toolbar to be created.
 * @author akiran
 */
public class ToolBar
{
	/**
	 * Items for the toolbar.
	 */
	private List<ToolBarIcon> items = new ArrayList<>();

	public List<ToolBarIcon> getItems()
	{
		return items;
	}

	public void setItems(List<ToolBarIcon> items)
	{
		this.items = items;
	}
	
	public void addItem(ToolBarIcon item)
	{
		this.items.add(item);
	}
	
	public void addDropDownItem(ToolBarDropDownIcon item)
	{
		this.items.add(item);
	}

	public void addToggleItem(ToolBarToggleIcon item)
	{
		this.items.add(item);
	}

	public JToolBar toJToolBar(ActionCollection actionCollection)
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		for(ToolBarIcon item : items)
		{
			if("-".equals(item.getIcon()))
			{
				toolBar.addSeparator();
				continue;
			}
			
			toolBar.add(item.toButton(actionCollection));
		}
		
		return toolBar;
	}
}
