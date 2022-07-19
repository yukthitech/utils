package com.yukthitech.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * Menu item for drop down button with user-data support.
 * @author akiran
 */
public class DropDownItem extends JMenuItem
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Custom user data linked to this item.
	 */
	private Object userData;

	/**
	 * Instantiates a new drop down item.
	 */
	public DropDownItem()
	{
		super();
	}

	/**
	 * Instantiates a new drop down item.
	 *
	 * @param icon the icon
	 */
	public DropDownItem(Icon icon)
	{
		super(icon);
	}

	/**
	 * Instantiates a new drop down item.
	 *
	 * @param text the text
	 * @param icon the icon
	 */
	public DropDownItem(String text, Icon icon)
	{
		super(text, icon);
	}

	/**
	 * Instantiates a new drop down item.
	 *
	 * @param text the text
	 */
	public DropDownItem(String text)
	{
		super(text);
	}

	/**
	 * Gets the user data.
	 *
	 * @return the user data
	 */
	public Object getUserData()
	{
		return userData;
	}

	/**
	 * Sets the user data.
	 *
	 * @param userData the new user data
	 */
	public void setUserData(Object userData)
	{
		this.userData = userData;
	}
}
