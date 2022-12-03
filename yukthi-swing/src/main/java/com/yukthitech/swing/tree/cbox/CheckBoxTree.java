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

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.yukthitech.swing.tree.ILazyTreeDataProvider;
import com.yukthitech.swing.tree.LazyTreeModel;
import com.yukthitech.swing.tree.LazyTreeNode;

public class CheckBoxTree extends JTree
{
	private static final long serialVersionUID = 1L;
	
	private LazyTreeModel<CboxNodeData> model;
	
	private ILazyTreeDataProvider<CboxNodeData> dataProvider;
	
	public CheckBoxTree()
	{
		super.setBorder(new EmptyBorder(5, 5, 5, 5));

		CboxTreeCellRenderer renderer = new CboxTreeCellRenderer();
		super.setCellRenderer(renderer);
		
		super.setEditable(true);
		super.setShowsRootHandles(true);
		super.setRootVisible(false);
		
		super.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				TreePath path = event.getPath();
		        
				if (path.getLastPathComponent() instanceof LazyTreeNode)
				{
					LazyTreeNode<CboxNodeData> node = (LazyTreeNode<CboxNodeData>) path.getLastPathComponent();
					node.loadChildren(model, false);
		        }
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}
		});
	}
	
	public void reload(LazyTreeNode<CboxNodeData> node)
	{
		node.loadChildren(model, true);
	}
	
	@SuppressWarnings({"unchecked" })
	public void setDataProvider(ILazyTreeDataProvider<CboxNodeData> dataProvider, ICboxTreeStateManager stateManager)
	{
		this.dataProvider = dataProvider;
		
		model = new LazyTreeModel<CboxNodeData>(dataProvider);
		super.setModel(model);
		
		CboxTreeCellEditor editor = new CboxTreeCellEditor(model, stateManager, this);
		super.setCellEditor(editor);

		LazyTreeNode<CboxNodeData> rootNode = (LazyTreeNode<CboxNodeData>) model.getRoot();
		rootNode.setChildren(dataProvider.getChildNodes(rootNode.getData()), model);
	}
	
	public ILazyTreeDataProvider<CboxNodeData> getDataProvider()
	{
		return dataProvider;
	}

	public boolean isPathEditable(TreePath path)
	{
		Object comp = path.getLastPathComponent();

		if(comp instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
			return !node.isRoot();
		}

		return false;
	}
}
