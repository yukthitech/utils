package com.yukthitech.autox.ide.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents a menu.
 * @author akiran
 */
public class Menu implements Validateable
{
	/**
	 * Label for the menu.
	 */
	private String label;
	
	/**
	 * Mnemonic for the menu.
	 */
	private char mnemonic = 0;
	
	/**
	 * Menu items for the label.
	 */
	private List<Object> menuItems;

	/**
	 * Gets the label for the menu.
	 *
	 * @return the label for the menu
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label for the menu.
	 *
	 * @param label the new label for the menu
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the mnemonic for the menu.
	 *
	 * @return the mnemonic for the menu
	 */
	public char getMnemonic()
	{
		return mnemonic;
	}

	/**
	 * Sets the mnemonic for the menu.
	 *
	 * @param mnemonic the new mnemonic for the menu
	 */
	public void setMnemonic(char mnemonic)
	{
		this.mnemonic = mnemonic;
	}

	/**
	 * Adds the menu item.
	 *
	 * @param menuItem the menu item
	 */
	public void addItem(MenuItem menuItem)
	{
		if(this.menuItems == null)
		{
			this.menuItems = new ArrayList<>();
		}
		
		this.menuItems.add(menuItem); 
	}
	
	public void addMenu(Menu menu)
	{
		if(this.menuItems == null)
		{
			this.menuItems = new ArrayList<>();
		}
		
		this.menuItems.add(menu); 
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isBlank(label))
		{
			label = "";
		}
		
		if(CollectionUtils.isEmpty(menuItems))
		{
			throw new ValidateException("No menu item specified for menu.");
		}
	}
	
	public JMenu toJMenu(ActionCollection actionCollection)
	{
		JMenu menu = new JMenu(label);
		
		if(mnemonic > 0)
		{
			menu.setMnemonic(mnemonic);
		}
		
		for(Object itemObj : this.menuItems)
		{
			if(itemObj instanceof Menu)
			{
				Menu submenu = (Menu) itemObj;
				menu.add(submenu.toJMenu(actionCollection));
				continue;
			}
			
			MenuItem item = (MenuItem) itemObj;
			
			if("-".equals(item.getLabel()))
			{
				menu.addSeparator();
				continue;
			}
			
			menu.add(item.toJMenuItem(actionCollection));
		}

		return menu;
	}
	
	public JPopupMenu toPopupMenu(ActionCollection actionCollection)
	{
		JPopupMenu popupMenu = new JPopupMenu();

		for(Object itemObj : this.menuItems)
		{
			if(itemObj instanceof Menu)
			{
				Menu submenu = (Menu) itemObj;
				popupMenu.add(submenu.toJMenu(actionCollection));
				continue;
			}
			
			MenuItem item = (MenuItem) itemObj;
			
			if("-".equals(item.getLabel()))
			{
				popupMenu.addSeparator();
				continue;
			}
			
			popupMenu.add(item.toJMenuItem(actionCollection));
		}
		
		return popupMenu;
	}
	
}
