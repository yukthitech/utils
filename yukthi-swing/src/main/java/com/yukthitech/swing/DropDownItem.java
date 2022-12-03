/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public DropDownItem cloneItem()
	{
		DropDownItem clone = new DropDownItem(super.getText(), super.getIcon());
		clone.setToolTipText(super.getToolTipText());
		clone.userData = this.userData;
		
		return clone;
	}
	
}
