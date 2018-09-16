package com.yukthitech.autox.ide.layout;

import javax.swing.JButton;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Toolbar icon.
 * @author akiran
 */
public class ToolBarIcon implements Validateable
{
	/**
	 * Icon to be displayed.
	 */
	private String icon;
	
	/**
	 * Tooltip for the button.
	 */
	private String tooltip;
	
	/**
	 * Action to be invoked.
	 */
	private String action;

	/**
	 * Gets the icon to be displayed.
	 *
	 * @return the icon to be displayed
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Sets the icon to be displayed.
	 *
	 * @param icon the new icon to be displayed
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	 * Gets the tooltip for the button.
	 *
	 * @return the tooltip for the button
	 */
	public String getTooltip()
	{
		return tooltip;
	}

	/**
	 * Sets the tooltip for the button.
	 *
	 * @param tooltip the new tooltip for the button
	 */
	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	/**
	 * Gets the action to be invoked.
	 *
	 * @return the action to be invoked
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * Sets the action to be invoked.
	 *
	 * @param action the new action to be invoked
	 */
	public void setAction(String action)
	{
		this.action = action;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(StringUtils.isEmpty(icon))
		{
			throw new ValidateException("Icon can not be empty.");
		}
	}
	
	public JButton toButton(ActionCollection actionCollection)
	{
		JButton button = new JButton();
		button.setIcon(IdeUtils.loadIcon(icon, 20));
		button.setToolTipText(tooltip);
		button.setBorder(null);

		if(action != null)
		{
			button.addActionListener(actionCollection.getActionListener(action));
		}
		
		return button;
	}
}
