package com.yukthitech.autox.ide.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuBar;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Menu bar for the ide.
 * @author akiran
 */
public class MenuBar implements Validateable
{
	/**
	 * Menus of the menu bar.
	 */
	private List<Menu> menus;

	/**
	 * Gets the menus of the menu bar.
	 *
	 * @return the menus of the menu bar
	 */
	public List<Menu> getMenus()
	{
		return menus;
	}

	/**
	 * Sets the menus of the menu bar.
	 *
	 * @param menus the new menus of the menu bar
	 */
	public void setMenus(List<Menu> menus)
	{
		this.menus = menus;
	}
	
	/**
	 * Adds the menu.
	 *
	 * @param menu the menu
	 */
	public void addMenu(Menu menu)
	{
		if(this.menus == null)
		{
			this.menus = new ArrayList<>();
		}
		
		this.menus.add(menu);
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(CollectionUtils.isEmpty(menus))
		{
			throw new ValidateException("Menus can not be null.");
		}
	}
	
	public JMenuBar toJMenuBar(ActionCollection actionCollection)
	{
		JMenuBar jmBar = new JMenuBar();
		
		for(Menu menu : this.menus)
		{
			jmBar.add(menu.toJMenu(actionCollection));
		}
		
		return jmBar;
	}
}
