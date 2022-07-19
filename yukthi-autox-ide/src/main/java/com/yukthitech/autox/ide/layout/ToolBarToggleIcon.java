package com.yukthitech.autox.ide.layout;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.swing.ToggleIconButton;

/**
 * Toolbar icon.
 * @author akiran
 */
public class ToolBarToggleIcon extends ToolBarIcon
{
	/**
	 * To button.
	 *
	 * @param actionCollection the action collection
	 * @return the j component
	 */
	public JComponent toButton(ActionCollection actionCollection)
	{
		JToggleButton button = new ToggleIconButton();
		button.setIcon(IdeUtils.loadIconWithoutBorder(icon, 20));
		button.setToolTipText(tooltip);

		if(action != null)
		{
			button.addActionListener(actionCollection.getActionListener(action));
		}
		
		String id = super.getId();
		
		if(id != null)
		{
			UiIdElementsManager.registerElement(id, button);
		}
		
		return button;
	}
}
