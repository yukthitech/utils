package com.yukthitech.autox.ide;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Tab component used by file editor.
 * @author akiran
 */
public class MaximizableTabbedPaneTab extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Color INACTIVE_COLOR = new Color(128, 128, 128);
	
	private static final Color ACTIVE_COLOR = Color.red;
	
	private static MouseListener CLOSE_BUTTON_MOUSE_LIST = new MouseAdapter()
	{
		public void mouseEntered(MouseEvent e) 
		{
			JButton button = (JButton) e.getComponent();
			button.setForeground(ACTIVE_COLOR);
		}
		
		public void mouseExited(MouseEvent e) 
		{
			JButton button = (JButton) e.getComponent();
			button.setForeground(INACTIVE_COLOR);
		}
	};

	protected JLabel changeLabel = new JLabel();
	
	protected JLabel label = new JLabel();
	
	private JButton closeButton = new JButton("\u2718");
	
	private MaximizableTabbedPane parentTabbedPane;
	
	private Component component;
	
	private IMaximizationListener maximizationListener;
	
	public MaximizableTabbedPaneTab(String text, MaximizableTabbedPane parentPane, Component component, IMaximizationListener maximizationListener)
	{
		this.parentTabbedPane = parentPane;
		this.component = component;
		this.maximizationListener = maximizationListener;
		
		super.setOpaque(false);
		
		label.setText(text);
		label.setBackground(null);
		
		closeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		closeButton.setBackground(null);
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusable(false);
		closeButton.setBorderPainted(false);
		closeButton.setRolloverEnabled(true);
		closeButton.setForeground(INACTIVE_COLOR);
		closeButton.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		closeButton.addMouseListener(CLOSE_BUTTON_MOUSE_LIST);
		closeButton.setToolTipText("Close");

		super.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		super.add(changeLabel);
		super.add(label);
		super.add(closeButton);
		
		init();
	}
	
	private void init()
	{
		closeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				closeTab();
			}
		});
		
		super.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				activateTab();
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(!e.isPopupTrigger())
				{
					return;
				}
				
				displayPopup(e);
			}
			
			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount() >= 2)
				{
					maximizationListener.flipMaximizationStatus(parentTabbedPane);
				}
			}
		});
	}
	
	protected void closeTab()
	{
		parentTabbedPane.remove(component);
		checkForMaximizationStatus();
	}
	
	protected void activateTab()
	{
		parentTabbedPane.setSelectedComponent(component);
	}
	
	protected void checkForMaximizationStatus()
	{
		int childCount = parentTabbedPane.getTabCount();
		
		if(childCount <= 0)
		{
			maximizationListener.minimize(parentTabbedPane);
		}
	}
	
	protected void displayPopup(MouseEvent e)
	{
		/*
		if(popupMenu == null)
		{
			 popupMenu = uiLayout.getPopupMenu("fileTabPopup").toPopupMenu(actionCollection);
		}
		
		ideContext.setActiveDetails(project, file);
		popupMenu.show(this, e.getX(), e.getY());
		*/
	}
}
