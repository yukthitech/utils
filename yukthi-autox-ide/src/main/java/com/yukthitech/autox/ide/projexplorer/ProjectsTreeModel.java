package com.yukthitech.autox.ide.projexplorer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.yukthitech.autox.ide.model.Project;

public class ProjectsTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	DefaultMutableTreeNode rootNode;
	
	public ProjectsTreeModel()
	{
		super(new DefaultMutableTreeNode("Root"));
		rootNode = (DefaultMutableTreeNode) super.getRoot();
	}
	
	public void addProject(ProjectTreeNode projectTreeNode)
	{
		rootNode.add(projectTreeNode);
		reload();
	}
	
	public ProjectTreeNode getProjectNode(Project project)
	{
		int count = rootNode.getChildCount();
		
		for(int i = 0; i < count; i++)
		{
			ProjectTreeNode node = (ProjectTreeNode) root.getChildAt(i);
			
			if(node.getProject() == project)
			{
				return node;
			}
		}
		
		return null;
	}
}
