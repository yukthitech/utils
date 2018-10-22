package com.yukthitech.autox.ide.ui;

import java.io.File;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.FolderTreeNode;

public class TestSuiteFolderTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	public TestSuiteFolderTreeNode(Project project, String name, File testSuiteFolder)
	{
		super(project, name, testSuiteFolder);
		super.setIcon(IdeUtils.loadIcon("/ui/icons/settings.png", 20));
		reload(false);
	}
}