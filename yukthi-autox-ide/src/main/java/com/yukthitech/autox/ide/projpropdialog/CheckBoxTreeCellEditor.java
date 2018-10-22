package com.yukthitech.autox.ide.projpropdialog;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.yukthitech.autox.ide.*;

public class CheckBoxTreeCellEditor extends DefaultCellEditor
{

	protected NodeData nodeData;

	public CheckBoxTreeCellEditor()
	{
		super(new JCheckBox());
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
	{
		JCheckBox editor = null;
		nodeData = getValue(value);
		if(nodeData != null)
		{
			editor = (JCheckBox) (super.getComponent());
			editor.setText(nodeData.getValue());
			editor.setSelected(nodeData.isChecked());
		}
		return editor;
	}

	public static NodeData getValue(Object value)
	{
		if(value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if(userObject instanceof NodeData)
			{
				return (NodeData) userObject;
			}
		}
		return null;
	}

	public Object getCellEditorValue()
	{
		JCheckBox editor = (JCheckBox) (super.getComponent());
		nodeData.setChecked(editor.isSelected());
		return nodeData;
	}

}