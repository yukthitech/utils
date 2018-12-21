package com.yukthitech.autox.ide.ui;

import java.awt.Component;
import java.awt.Image;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.yukthitech.autox.ide.IdeUtils;

public class BaseTreeNodeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private static Image ERROR_ICON = IdeUtils.loadIcon("/ui/icons/bookmark_error.png", 12).getImage();
	
	private static Image WARN_ICON = IdeUtils.loadIcon("/ui/icons/bookmark_warn.png", 12).getImage();
	
	private boolean hasErrors = false;
	
	private boolean hasWarnings = false;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		DefaultTreeCellRenderer component = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		if(!(value instanceof BaseTreeNode))
		{
			return component;
		}
		
		BaseTreeNode node = (BaseTreeNode) value;
		component.setIcon(node.getIcon());
		component.setText(node.getLabel());
		
		this.hasErrors = node.isErrored();
		this.hasWarnings = node.isWarned();
		
		return component;
	}
	
	protected void paintComponent(java.awt.Graphics g) 
	{
		super.paintComponent(g);
		
		if(hasErrors)
		{
			g.drawImage(ERROR_ICON, -5, -5, this);
		}
		else if(hasWarnings)
		{
			g.drawImage(WARN_ICON, -5, -5, this);
		}
	}
}
