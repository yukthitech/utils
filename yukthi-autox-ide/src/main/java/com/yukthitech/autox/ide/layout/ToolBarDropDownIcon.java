package com.yukthitech.autox.ide.layout;

import javax.swing.JComponent;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.swing.DropDownButton;

/**
 * Toolbar icon.
 * @author akiran
 */
public class ToolBarDropDownIcon extends ToolBarIcon
{
	/**
	 * To button.
	 *
	 * @param actionCollection the action collection
	 * @return the j component
	 */
	public JComponent toButton(ActionCollection actionCollection)
	{
		DropDownButton button = new DropDownButton();
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
