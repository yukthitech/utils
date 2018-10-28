package com.yukthitech.autox.ide.projpropdialog;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;

import com.yukthitech.autox.ide.model.Project;

public class TestSuitesFolderTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;
	
	private TestSuitesFolderTreeNode rootNode;
	private Project project;

	public TestSuitesFolderTreeModel(File file, Project project)
	{
		super(new TestSuitesFolderTreeNode(file.getName(), file));
		this.rootNode = (TestSuitesFolderTreeNode) super.getRoot();
		this.project = project;

	}

	public Set<File> getSelectedFolders()
	{
		Set<File> res = new HashSet<File>();
		rootNode.getSelectedFolders(res, project, null);
		
		return res;
	}

	public void setSelectedFolders(Set<File> testSuitesFoldersList)
	{
		traverse(rootNode, testSuitesFoldersList, new File(project.getBaseFolderPath()));
	}

	public void traverse(TestSuitesFolderTreeNode node, Set<File> testSuitesFolderList, File projectFolder)
	{
		if(testSuitesFolderList != null)
		{
			if(testSuitesFolderList.contains(node.getFolder()))
			{
				node.setSelectedFolder();
			}
			
			for(int i = 0; i < node.getChildCount(); i++)
			{
				traverse((TestSuitesFolderTreeNode) node.getChildAt(i), testSuitesFolderList, projectFolder);
			}
		}
	}

}
