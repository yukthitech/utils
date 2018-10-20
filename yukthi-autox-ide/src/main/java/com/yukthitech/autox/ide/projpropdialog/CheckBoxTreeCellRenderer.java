package com.yukthitech.autox.ide.projpropdialog;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.yukthitech.autox.ide.*;

public class CheckBoxTreeCellRenderer extends DefaultTreeCellRenderer {

	  protected JCheckBox checkBoxRenderer = new JCheckBox();

	  public Component getTreeCellRendererComponent(JTree tree, Object value,
	      boolean selected, boolean expanded, boolean leaf, int row,
	      boolean hasFocus) {
	    if (value instanceof DefaultMutableTreeNode) {
	      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	      Object userObject = node.getUserObject();
	      if (userObject instanceof NodeData) {
	        NodeData question = (NodeData) userObject;
	        prepareCheckBoxRenderer(question, selected);
	        return checkBoxRenderer;
	      }
	    }
	    return super.getTreeCellRendererComponent(tree, value, selected, expanded,
	        leaf, row, hasFocus);
	  }

	  protected void prepareCheckBoxRenderer(NodeData tfq, boolean selected) {
	    checkBoxRenderer.setText(tfq.getValue());
	    checkBoxRenderer.setSelected(tfq.isChecked());
	    if (selected) {
	      checkBoxRenderer.setForeground(getTextSelectionColor());
	      checkBoxRenderer.setBackground(getBackgroundSelectionColor());
	    } else {
	      checkBoxRenderer.setForeground(getTextNonSelectionColor());
	      checkBoxRenderer.setBackground(getBackgroundNonSelectionColor());
	    }
	    
	  }

	}