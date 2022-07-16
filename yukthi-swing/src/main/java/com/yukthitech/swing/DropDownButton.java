package com.yukthitech.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.yukthitech.swing.common.SwingUtils;

/**
 * A button which can support drop down. This button has 2 parts
 * 		main button - If no last action is present, deafult action will get executed if specified.
 * 		arrow button - this will display the popup.
 * 
 * Popup items
 *  - Recently executed item will come on top
 *  - Last executed item will execute by default when main button is clicked
 *  
 * Static popup items
 *  - These items will remain static
 * 
 * @author akiran
 */
public class DropDownButton extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Border thickness.
	 */
	private static final int BORDER_THICKNESS = 3;
	
	/**
	 * Empty border.
	 */
	private static final Border EMPTY_BORDER = new EmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS);
	
	/**
	 * Etched border with empty boundaries.
	 */
	private static final Border ETCHED_BORDER = new CompoundBorder(
			new EtchedBorder(EtchedBorder.LOWERED, null, null), 
			new EmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
	
	private static final Icon DROP_DOWN_ICON = SwingUtils.loadIconWithoutBorder("/swing-icons/drop-down-arrow.svg", 8);
	
	/**
	 * Main action button.
	 */
	private JButton mainButton;
	
	/**
	 * Arrow button which would display the popup.
	 */
	private JButton arrowButton;
	
	/**
	 * Drop down popup menu.
	 */
	private JPopupMenu popupMenu = new JPopupMenu();
	
	/**
	 * Maximum number of items allowed for this button.
	 */
	private int maximumItemCount = 10;
	
	/**
	 * Standard menu items to be shown in drop down. As more than {@link #maximumItemCount} items are
	 * added, the least used items will be removed.
	 */
	private List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
	
	/**
	 * Static menu items to be shown in drop down. These items will never get removed and gets grouped at end.
	 */
	private List<JMenuItem> staticMenuItems = new ArrayList<JMenuItem>();

	/**
	 * Default action to be invoked when there is no last action.
	 */
	private ActionListener defaultAction;
	
	/**
	 * Invoked when popup item is clicked. And this will take care of reordering.
	 */
	private ActionListener popupItemClicked = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JMenuItem item = (JMenuItem) e.getSource();
			
			synchronized(DropDownButton.this)
			{
				menuItems.remove(item);
				menuItems.add(0, item);
				
				resetMenuItems();
			}
		}
	};
	
	/**
	 * Mouse listener to highlight the border.
	 */
	private MouseListener borderHighlighter = new MouseAdapter()
	{
		public void mouseEntered(java.awt.event.MouseEvent e) 
		{
			mainButton.setBorder(ETCHED_BORDER);
			arrowButton.setBorder(ETCHED_BORDER);
		};
		
		public void mouseExited(java.awt.event.MouseEvent e) 
		{
			mainButton.setBorder(EMPTY_BORDER);
			arrowButton.setBorder(EMPTY_BORDER);
		};
	};
	
	/**
	 * Instantiates a new drop down button.
	 */
	public DropDownButton()
	{
		this("", null);
	}
	
	/**
	 * Instantiates a new drop down button.
	 *
	 * @param text the text
	 * @param icon the icon
	 */
	public DropDownButton(String text, Icon icon)
	{
		mainButton = new JButton(text, icon);
		arrowButton = new JButton(DROP_DOWN_ICON);

		mainButton.setBackground(null);
		arrowButton.setBackground(null);
		mainButton.setContentAreaFilled(false);
		arrowButton.setContentAreaFilled(false);
		arrowButton.setFocusable(false);
		mainButton.setFocusable(false);
		mainButton.setBorder(EMPTY_BORDER);
		arrowButton.setBorder(EMPTY_BORDER);
		
		super.setOpaque(false);
		
		mainButton.addMouseListener(borderHighlighter);
		arrowButton.addMouseListener(borderHighlighter);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		super.setLayout(gridBagLayout);
		
		GridBagConstraints mainButConst = new GridBagConstraints();
		mainButConst.fill = GridBagConstraints.BOTH;
		mainButConst.gridx = 0;
		mainButConst.gridy = 0;
		add(mainButton, mainButConst);
		
		GridBagConstraints drpDwnConst = new GridBagConstraints();
		drpDwnConst.fill = GridBagConstraints.BOTH;
		drpDwnConst.gridx = 1;
		drpDwnConst.gridy = 0;
		add(arrowButton, drpDwnConst);
		
		
		mainButton.addActionListener(this::onMainButtonClicked);
		arrowButton.addActionListener(this::onArrowButtonClicked);
	}

	/**
	 * Sets the maximum number of items allowed for this button.
	 *
	 * @param maximumItemCount the new maximum number of items allowed for this
	 *        button
	 */
	public void setMaximumItemCount(int maximumItemCount)
	{
		this.maximumItemCount = maximumItemCount;
	}
	
	/**
	 * Gets the maximum number of items allowed for this button.
	 *
	 * @return the maximum number of items allowed for this button
	 */
	public int getMaximumItemCount()
	{
		return maximumItemCount;
	}
	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText()
	{
		return mainButton.getText();
	}
	
	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text)
	{
		this.mainButton.setText(text);
	}
	
	/**
	 * Gets the icon of main button.
	 *
	 * @return the icon
	 */
	public Icon getIcon()
	{
		return mainButton.getIcon();
	}
	
	/**
	 * Sets the icon of main button.
	 *
	 * @param icon the new icon
	 */
	public void setIcon(Icon icon)
	{
		mainButton.setIcon(icon);
	}
	
	/**
	 * Adds the popup menu item. Once limit is reached, least used item will be removed.
	 *
	 * @param item the item
	 */
	public void addItem(JMenuItem item)
	{
		if(item == null || menuItems.contains(item))
		{
			return;
		}
		
		if(popupMenu == null)
		{
			popupMenu = new JPopupMenu();
			popupMenu.setBorder(new EtchedBorder());
		}
		
		popupMenu.add(item, 0);
		menuItems.add(0, item);
		item.addActionListener(popupItemClicked);
		
		int itemCount = popupMenu.getComponentCount();
		
		if(itemCount > maximumItemCount)
		{
			removeItem((JMenuItem) popupMenu.getComponent(itemCount - 1));
		}
	}
	
	/**
	 * Adds the static menu item.
	 *
	 * @param item the item
	 */
	public void addStaticMenuItem(JMenuItem item)
	{
		if(staticMenuItems.contains(item))
		{
			return;
		}
		
		if(staticMenuItems.isEmpty())
		{
			popupMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		}
		
		popupMenu.add(item);
		staticMenuItems.add(item);
	}
	
	/**
	 * Removes the specified popup item.
	 *
	 * @param item the item
	 */
	public void removeItem(JMenuItem item)
	{
		if(!menuItems.remove(item))
		{
			return;
		}
		
		popupMenu.remove(item);
		item.removeActionListener(popupItemClicked);
	}
	
	/**
	 * Resets the menu items in the popup.
	 */
	private void resetMenuItems()
	{
		popupMenu.removeAll();
		menuItems.forEach(item -> popupMenu.add(item));
		
		if(!staticMenuItems.isEmpty())
		{
			popupMenu.add(new JSeparator(JSeparator.HORIZONTAL));
			staticMenuItems.forEach(item -> popupMenu.add(item));
		}
	}
	
	/**
	 * Gets the popup items.
	 *
	 * @return the items
	 */
	public List<JMenuItem> getItems()
	{
		return Collections.unmodifiableList(menuItems);
	}
	
	/**
	 * Sets the default action to be invoked when there is no last action.
	 *
	 * @param action the new default action to be invoked when there is no last
	 *        action
	 */
	public void setDefaultAction(ActionListener action)
	{
		this.defaultAction = action;
	}
	
	private void onMainButtonClicked(ActionEvent e)
	{
		JMenuItem lastAction = popupMenu.getComponentCount() > 0 ? (JMenuItem) popupMenu.getComponent(0) : null;
		
		if(lastAction == null)
		{
			if(defaultAction != null)
			{
				defaultAction.actionPerformed(e);
			}
			
			return;
		}
		
		lastAction.doClick();
	}
	
	private void onArrowButtonClicked(ActionEvent e)
	{
		if(menuItems.isEmpty() && staticMenuItems.isEmpty())
		{
			return;
		}
		
		popupMenu.show(this, 0, super.getHeight());
	}
}
