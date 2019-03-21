package com.yukthitech.autox.ide.projexplorer;

import java.io.File;

import com.yukthitech.autox.ide.model.Project;

public class TestFolderTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	public TestFolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		super(id, projectExplorer, project, name, folder);
	}

	@Override
	protected FolderTreeNode newFolderTreeNode(String id, ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		return new TestFolderTreeNode(id, projectExplorer, project, name, folder);
	}
}
