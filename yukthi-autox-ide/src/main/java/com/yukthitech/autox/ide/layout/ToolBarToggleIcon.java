package com.yukthitech.autox.ide.layout;

import javax.swing.JComponent;
import javax.swing.JToggleButton;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.swing.ToolbarToggleButton;

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
		JToggleButton button = new ToolbarToggleButton();
		button.setIcon(IdeUtils.loadIcon(icon, 20));
		button.setToolTipText(tooltip);
		button.setBorder(null);

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
