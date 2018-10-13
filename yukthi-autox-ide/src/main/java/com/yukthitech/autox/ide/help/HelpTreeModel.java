package com.yukthitech.autox.ide.help;

import javax.swing.tree.DefaultTreeModel;

public class HelpTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;

	public HelpTreeModel(HelpNodeData nodeData)
	{
		super(new HelpTreeNode(nodeData));
	}

}
