package com.yukthitech.swing.tree;

import javax.swing.tree.DefaultTreeModel;

public class LazyTreeModel<N> extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	public LazyTreeModel(ILazyTreeDataProvider<N> dataProvider)
	{
		super(new LazyTreeNode<N>(dataProvider.getRootNode(), dataProvider));
	}
	
}
