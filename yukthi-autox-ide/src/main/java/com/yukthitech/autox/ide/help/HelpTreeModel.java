package com.yukthitech.autox.ide.help;

import javax.swing.tree.DefaultTreeModel;

public class HelpTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	private HelpTreeNode root;

	public HelpTreeModel(HelpNodeData nodeData)
	{
		super(new HelpTreeNode(nodeData));
		root = (HelpTreeNode) super.getRoot();
	}
	
	
	public HelpTreeNode getNode(String id)
	{
		return root.getNode(id);
	}

}
