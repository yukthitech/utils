package com.yukthitech.autox.ide.ui;

import java.io.File;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.FileTreeNode;
import com.yukthitech.autox.ide.projexplorer.FolderTreeNode;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;

public class TestSuiteFolderTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	public TestSuiteFolderTreeNode(ProjectExplorer projectExplorer, Project project, String name, File testSuiteFolder)
	{
		super(projectExplorer, project, name, testSuiteFolder);
		
		super.setIcon(IdeUtils.loadIcon("/ui/icons/settings.png", 20));
		reload(false);
	}
	
	public FileTreeNode getFileNode(File file, String path[], int index)
	{
		String relativePath = IdeFileUtils.getRelativePath(super.getFolder(), file);
		
		if(relativePath == null || relativePath.length() == 0)
		{
			return null;
		}
		
		String newPath[] = relativePath.split("\\" + File.separator);
		return super.getFileNode(file, newPath, 0);
	}	
}