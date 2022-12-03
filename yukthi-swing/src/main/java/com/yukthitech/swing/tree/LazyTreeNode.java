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
package com.yukthitech.swing.tree;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.swing.common.SwingUtils;

public class LazyTreeNode<N> extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	
	private ILazyTreeDataProvider<N> dataProvider;
	
	private N data;
	
	private boolean loaded = false;

	public LazyTreeNode(N data, ILazyTreeDataProvider<N> dataProvider)
	{
		this.data = data;
		this.dataProvider = dataProvider;
		
		super.setUserObject(data);
		
		super.add(new DefaultMutableTreeNode("Loading...", false));
		this.loaded = false;
	}
	
	public N getData()
	{
		return data;
	}
	
	public void setChildren(List<N> childNodes, LazyTreeModel<N> model)
	{
		if(loaded)
		{
			return ;
		}
		
		removeAllChildren();
		
		if(CollectionUtils.isNotEmpty(childNodes))
		{
			childNodes.forEach(cdata -> add(new LazyTreeNode<N>(cdata, dataProvider)));
		}
		
		model.nodeStructureChanged(LazyTreeNode.this);
		loaded = true;
	}
	
	public void loadChildren(LazyTreeModel<N> model, boolean forced)
	{
		if(forced)
		{
			this.loaded = false;
		}
		
		SwingUtils.executeUiTask(() -> 
		{
			List<N> childNodes = dataProvider.getChildNodes(data);
			setChildren(childNodes, model);
		});
	}
}
