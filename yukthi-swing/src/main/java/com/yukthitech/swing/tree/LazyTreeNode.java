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
