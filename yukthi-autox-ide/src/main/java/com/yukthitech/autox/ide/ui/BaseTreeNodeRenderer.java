package com.yukthitech.autox.ide.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class BaseTreeNodeRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer
{
	private static final long serialVersionUID = 1L;

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
		
		return component;
	}
	
}
