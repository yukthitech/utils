package com.yukthitech.autox.ide.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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
	
	
}
