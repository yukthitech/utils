package com.yukthitech.swing;

import java.awt.Dimension;
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
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.yukthitech.autox.ide.IdeUtils;

/**
 * A button which can support drop down. This button has 2 parts
 * 		main button - If no explicit action listeners are added, then this will execute the previously executed action from drop-down.
 * 		arrow button - this will display the popup.
 * 
 * Note action listener will be added to all menu-items of the popup. To track which was last action. So if there is any change in popup menu, it should reset on this widget.
 * @author akiran
 */
public class DropDownButton extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Border ETCHED_BORDER = new EtchedBorder();
	
	private JButton mainButton;
	
	private JButton arrowButton;
	
	private JPopupMenu popupMenu;
	
	/**
	 * Flag indicating if custom listeners are set.
	 */
	private boolean customActionListenerSet = false;
	
	/**
	 * Tracks the last action menu item.
	 */
	private JMenuItem lastAction;
	
	/**
	 * Current list of menu items.
	 */
	private List<JMenuItem> menuItems = new ArrayList<>();
	
	private ActionListener popupItemClicked = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			lastAction = (JMenuItem) e.getSource();
		}
	};
	
	private MouseListener borderHighlighter = new MouseAdapter()
	{
		public void mouseEntered(java.awt.event.MouseEvent e) 
		{
			mainButton.setBorder(ETCHED_BORDER);
			arrowButton.setBorder(ETCHED_BORDER);
		};
		
		public void mouseExited(java.awt.event.MouseEvent e) 
		{
			mainButton.setBorder(null);
			arrowButton.setBorder(null);
		};
	};
	
	public DropDownButton()
	{
		this("", null);
	}
	
	public DropDownButton(String text, Icon icon)
	{
		mainButton = new JButton(text, icon);
		arrowButton = new JButton(IdeUtils.loadIcon("/ui/icons/drop-down-arrow.png", 8));
		
		mainButton.setBorder(null);
		arrowButton.setBorder(null);
		mainButton.setBackground(null);
		arrowButton.setBackground(null);
		mainButton.setContentAreaFilled(false);
		arrowButton.setContentAreaFilled(false);
		arrowButton.setFocusable(false);
		
		super.setOpaque(false);
		
		mainButton.addMouseListener(borderHighlighter);
		arrowButton.addMouseListener(borderHighlighter);
		
		GridBagLayout layout = new GridBagLayout();
		super.setLayout(layout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		super.add(mainButton, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0;
		super.add(arrowButton, c);
		
		mainButton.addActionListener(this::mainButtonClicked);
		arrowButton.addActionListener(this::arrowButtonClicked);
	}
	
	public String getText()
	{
		return mainButton.getText();
	}
	
	public void setText(String text)
	{
		this.mainButton.setText(text);
	}
	
	public Icon getIcon()
	{
		return mainButton.getIcon();
	}
	
	public void setIcon(Icon icon)
	{
		mainButton.setIcon(icon);
	}
	
	public void addItem(JMenuItem item)
	{
		if(item == null || menuItems.contains(item))
		{
			return;
		}
		
		if(popupMenu == null)
		{
			popupMenu = new JPopupMenu();
		}
		
		popupMenu.add(item);
		item.addActionListener(popupItemClicked);
		this.menuItems.add(item);
	}
	
	public void removeItem(JMenuItem item)
	{
		if(!this.menuItems.remove(item))
		{
			return;
		}
		
		popupMenu.remove(item);
		item.removeActionListener(popupItemClicked);
	}
	
	public List<JMenuItem> getItems()
	{
		return Collections.unmodifiableList(menuItems);
	}
	
	public void addActionListener(ActionListener actionListener)
	{
		this.mainButton.addActionListener(actionListener);
		
		if(actionListener != null)
		{
			customActionListenerSet = true;
		}
	}
	
	public void removeActionListener(ActionListener actionListener)
	{
		this.mainButton.removeActionListener(actionListener);
		
		ActionListener listeners[] = mainButton.getActionListeners();
		customActionListenerSet = (listeners != null && listeners.length > 0);
	}
	
	public ActionListener[] getActionListeners()
	{
		return mainButton.getActionListeners();
	}
	
	private void mainButtonClicked(ActionEvent e)
	{
		if(customActionListenerSet)
		{
			return;
		}
		
		if(lastAction == null)
		{
			return;
		}
		
		lastAction.doClick();
	}
	
	private void arrowButtonClicked(ActionEvent e)
	{
		if(popupMenu == null)
		{
			return;
		}
		
		popupMenu.show(this, 0, super.getHeight());
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		Dimension mainButDim = mainButton.getPreferredSize();
		Dimension arrowDim = arrowButton.getPreferredSize();
		
		return new Dimension(mainButDim.width + arrowDim.width, mainButDim.height);
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}
}
