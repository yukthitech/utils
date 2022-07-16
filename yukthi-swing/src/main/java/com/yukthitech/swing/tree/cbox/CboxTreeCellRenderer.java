package com.yukthitech.swing.tree.cbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.yukthitech.swing.common.SwingUtils;

public class CboxTreeCellRenderer extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private static final Icon EMPTY_ICON = SwingUtils.loadIconWithoutBorder("/swing-icons/checkbox-empty.svg", 20);
	
	private static final Icon PARTIAL_ICON = SwingUtils.loadIconWithoutBorder("/swing-icons/checkbox-partial.svg", 20);
	
	private static final Icon SELECTED_ICON = SwingUtils.loadIconWithoutBorder("/swing-icons/checkbox-selected.svg", 20);
	
	private static Color FIXED_COLOR = Color.LIGHT_GRAY;
	
	private static Font FIXED_FONT = new Font(Font.DIALOG, Font.BOLD | Font.ITALIC, 12);

	private static Color NORMAL_COLOR = Color.BLACK;
	
	private static Font NORMAL_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);

	private JCheckBox checkBoxRenderer = new JCheckBox();
	
	public CboxTreeCellRenderer()
	{
		checkBoxRenderer.setIcon(EMPTY_ICON);
		checkBoxRenderer.setSelectedIcon(SELECTED_ICON);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		if(value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();

			if(userObject instanceof CboxNodeData)
			{
				CboxNodeData data = (CboxNodeData) userObject;
				
				if(data.getStatus() == SelectStatus.PARTIALLY_SELECTED)
				{
					checkBoxRenderer.setSelectedIcon(PARTIAL_ICON);
				}
				else
				{
					checkBoxRenderer.setSelectedIcon(SELECTED_ICON);
				}

				prepareCheckBoxRenderer(tree, data, selected, checkBoxRenderer);
				return checkBoxRenderer;
			}
		}
		
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

	protected void prepareCheckBoxRenderer(JTree tree, CboxNodeData data, boolean selected, JComponent component)
	{
		((JCheckBox) component).setText(data.getLabel());
		((JCheckBox) component).setSelected(data.getStatus() != SelectStatus.NOT_SELECTED);
		
		if(selected)
		{
			component.setForeground(getTextSelectionColor());
			component.setBackground(getBackgroundSelectionColor());
		}
		else
		{
			component.setForeground(getTextNonSelectionColor());
			component.setBackground(getBackgroundNonSelectionColor());
		}
		
		if(data.isFixedStatus())
		{
			component.setFont(FIXED_FONT);
			component.setForeground(FIXED_COLOR);
			
			if(selected)
			{
				component.setForeground(Color.DARK_GRAY);
			}
		}
		else
		{
			component.setFont(NORMAL_FONT);
			component.setForeground(NORMAL_COLOR);
		}

		Dimension dimension = component.getMinimumSize();
		component.setPreferredSize(new Dimension(dimension.width + 20, dimension.height));
	}
}