package com.yukthitech.autox.ide.projpropdialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CheckBoxTreeCellRenderer extends DefaultTreeCellRenderer
{

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	protected JCheckBox checkBoxRenderer = new JCheckBox();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		if(value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();

			if(userObject instanceof NodeData)
			{
				NodeData data = (NodeData) userObject;
				prepareCheckBoxRenderer(tree, data, selected);

				if(data.isChecked() || childChecked(node))
				{
					checkBoxRenderer.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 15));
				}
				else
				{
					checkBoxRenderer.setFont(new Font("Arial", Font.PLAIN, 15));
				}
				
				return checkBoxRenderer;
			}
		}
		
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

	protected void prepareCheckBoxRenderer(JTree tree, NodeData data, boolean selected)
	{
		checkBoxRenderer.setText(data.getLable());
		checkBoxRenderer.setSelected(data.isChecked());
		
		if(selected)
		{
			checkBoxRenderer.setForeground(getTextSelectionColor());
			checkBoxRenderer.setBackground(getBackgroundSelectionColor());
		}
		else
		{
			checkBoxRenderer.setForeground(getTextNonSelectionColor());
			checkBoxRenderer.setBackground(getBackgroundNonSelectionColor());
		}

		Dimension dimension = checkBoxRenderer.getMinimumSize();
		checkBoxRenderer.setPreferredSize(new Dimension(dimension.width + 20, dimension.height));
	}

	protected boolean childChecked(DefaultMutableTreeNode node)
	{
		for(int i = 0; i < node.getChildCount(); i++)
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if(!child.isLeaf())
			{
				if(childChecked(child))
				{
					return true;
				}
			}
			
			Object userObject = child.getUserObject();
			
			if(userObject instanceof NodeData)
			{
				NodeData data = (NodeData) userObject;
			
				if(data.isChecked())
				{
					return true;
				}
			}
		}
		return false;
	}

}