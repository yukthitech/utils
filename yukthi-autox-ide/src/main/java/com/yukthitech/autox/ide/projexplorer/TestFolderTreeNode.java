package com.yukthitech.autox.ide.projexplorer;

import java.io.File;

import com.yukthitech.autox.ide.model.Project;

public class TestFolderTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	public TestFolderTreeNode(ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		super(projectExplorer, project, name, folder);
	}

	@Override
	protected FolderTreeNode newFolderTreeNode(ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		return new TestFolderTreeNode(projectExplorer, project, name, folder);
	}
}
