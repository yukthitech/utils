package com.yukthitech.autox.ide.help;

import javax.swing.tree.DefaultMutableTreeNode;

public class HelpTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	
	private HelpNodeData helpNodeData;
	
	public HelpTreeNode(HelpNodeData helpNodeData)
	{
		super(helpNodeData.getLabel());
		this.helpNodeData = helpNodeData;
		
		if(helpNodeData.getChildNodes() == null)
		{
			return;
		}
		
		for(HelpNodeData child : helpNodeData.getChildNodes())
		{
			if(!child.isFiltered())
			{
				continue;
			}
			
			super.add(new HelpTreeNode(child));
		}
	}

	public HelpNodeData getHelpNodeData()
	{
		return helpNodeData;
	}
}
