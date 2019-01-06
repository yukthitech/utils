package com.yukthitech.autox.ide;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 * Tabbed pane which support data for maximization.
 * @author akiran
 */
public class MaximizableTabbedPane extends JTabbedPane
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Parent split pane containing the split pane.
	 */
	private JSplitPane parentSplitPane;
	
	/**
	 * Flag indicating if this is left component or right.
	 */
	private boolean leftComponent;
	
	protected IMaximizationListener maximizationListener;
	
	private boolean viewsCloseable = true;
	
	public MaximizableTabbedPane()
	{
	}
	
	public void setParentDetails(IMaximizationListener listener, JSplitPane parentPane, boolean leftComp)
	{
		this.maximizationListener = listener;
		this.parentSplitPane = parentPane;
		this.leftComponent = leftComp;
	}

	public JSplitPane getParentSplitPane()
	{
		return parentSplitPane;
	}

	public boolean isLeftComponent()
	{
		return leftComponent;
	}
	
	public void setViewsCloseable(boolean viewsCloseable)
	{
		this.viewsCloseable = viewsCloseable;
	}
	
	@Override
	public void addTab(String title, Component component)
	{
		int nextTabIndex = super.getTabCount();
		super.addTab(title, component);
		
		super.setTabComponentAt(nextTabIndex, new MaximizableTabbedPaneTab(title, this, component, maximizationListener, viewsCloseable));
	}
	
	@Override
	public void addTab(String title, Icon icon, Component component)
	{
		int nextTabIndex = super.getTabCount();
		super.addTab(title, icon, component);
		
		super.setTabComponentAt(nextTabIndex, new MaximizableTabbedPaneTab(title, this, component, maximizationListener, viewsCloseable));
	}
	
	@Override
	public void addTab(String title, Icon icon, Component component, String tip)
	{
		int nextTabIndex = super.getTabCount();
		super.addTab(title, icon, component, tip);
		
		super.setTabComponentAt(nextTabIndex, new MaximizableTabbedPaneTab(title, this, component, maximizationListener, viewsCloseable));
	}
}
