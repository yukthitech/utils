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
	
	private static final int ICON_HEIGHT = 12;
	
	private static final int ICON_BORDER = 5;
	
	private static Image ERROR_ICON = IdeUtils.loadIcon("/ui/icons/bookmark_error.png", ICON_HEIGHT).getImage();
	
	private static Image WARN_ICON = IdeUtils.loadIcon("/ui/icons/bookmark_warn.png", ICON_HEIGHT).getImage();
	
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
		
		int height = super.getSize().height;
		
		if(hasErrors)
		{
			g.drawImage(ERROR_ICON, -ICON_BORDER, height - ICON_HEIGHT - 3, this);
		}
		else if(hasWarnings)
		{
			g.drawImage(WARN_ICON, -ICON_BORDER, height - ICON_HEIGHT - 3, this);
		}
	}
}
