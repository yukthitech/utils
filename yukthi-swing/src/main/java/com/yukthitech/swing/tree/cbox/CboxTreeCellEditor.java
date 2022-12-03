/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.swing.tree.cbox;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.yukthitech.swing.common.SwingUtils;

public class CboxTreeCellEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = 1L;
	
	private static final Icon EMPTY_ICON = SwingUtils.loadIconWithoutBorder("/swing-icons/checkbox-empty.svg", 20);
	
	private static final Icon SELECTED_ICON = SwingUtils.loadIconWithoutBorder("/swing-icons/checkbox-selected.svg", 20);
	
	protected CboxNodeData nodeData;
	
	private DefaultMutableTreeNode treeNode;
	
	private DefaultTreeModel model;
	
	private JTree source;
	
	private ICboxTreeStateManager stateManager;

	public CboxTreeCellEditor(DefaultTreeModel model, ICboxTreeStateManager stateManager, JTree source)
	{
		super(buildCheckBox());
		this.source = source;
		this.stateManager = stateManager;
		this.model = model;
	}
	
	private static JCheckBox buildCheckBox()
	{
		JCheckBox cbox = new JCheckBox();
		cbox.setIcon(EMPTY_ICON);
		cbox.setSelectedIcon(SELECTED_ICON);
		
		return cbox;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
	{
		JCheckBox editor = null;
		nodeData = getValue(value);
		
		if(nodeData != null)
		{
			editor = (JCheckBox) (super.getComponent());
			editor.setText(nodeData.getLabel());
			editor.setSelected(nodeData.getStatus() == SelectStatus.SELECTED);
		}
		
		return editor;
	}

	public CboxNodeData getValue(Object value)
	{
		if(value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			this.treeNode = node;
			
			Object userObject = node.getUserObject();
			
			if(userObject instanceof CboxNodeData)
			{
				return (CboxNodeData) userObject;
			}
		}
		
		return null;
	}
	
	private void updateParentData(boolean selected)
	{
		SwingUtils.executeUiTask(() -> 
		{
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeNode.getParent();
			List<CboxNodeData> nodesToRemove = new ArrayList<CboxNodeData>();
			
			while(parent != null)
			{
				if(!(parent.getUserObject() instanceof CboxNodeData))
				{
					break;
				}
				
				CboxNodeData parentData = (CboxNodeData) parent.getUserObject();
				
				//remove mid child nodes which were selected before
				nodesToRemove.forEach(data -> parentData.childSelectionChanged(data.getUserData(), false, stateManager));
				
				//if in middle, current parent is selected, mark it for removal from top hierarchy
				if(parentData.getStatus() == SelectStatus.SELECTED)
				{
					nodesToRemove.add(parentData);
				}
				
				parentData.childSelectionChanged(nodeData.getUserData(), selected, stateManager);
				model.nodeChanged(parent);
				
				parent = (DefaultMutableTreeNode) parent.getParent();
			}
		});
	}

	public Object getCellEditorValue()
	{
		JCheckBox editor = (JCheckBox) (super.getComponent());
		nodeData.setStatus(editor.isSelected() ? SelectStatus.SELECTED : SelectStatus.NOT_SELECTED, stateManager);
		updateParentData(editor.isSelected());
		
		return nodeData;
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent)
	{
		TreePath treePath = source.getSelectionPath();
		
		if(anEvent instanceof MouseEvent)
		{
			MouseEvent mevent = (MouseEvent) anEvent;
	        treePath = source.getPathForLocation(mevent.getX(), mevent.getY());
		}
		
		Object nodeData = treePath == null ? null : ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject();
		
		if(!(nodeData instanceof CboxNodeData))
		{
			return false;
		}
		
		CboxNodeData cboxData = (CboxNodeData) nodeData;
		return cboxData.isEditable();
	}

}