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
	
	public HelpTreeNode getNode(String id)
	{
		if(helpNodeData != null && id.equals(helpNodeData.getId()))
		{
			return this;
		}
		
		int count = super.getChildCount();
		
		if(count <= 0)
		{
			return null;
		}
		
		for(int i = 0; i < count; i++)
		{
			HelpTreeNode childNode = (HelpTreeNode) super.getChildAt(i);
			HelpTreeNode selectedNode = childNode.getNode(id);
			
			if(selectedNode != null)
			{
				return selectedNode;
			}
		}
		
		return null;
	}
}
