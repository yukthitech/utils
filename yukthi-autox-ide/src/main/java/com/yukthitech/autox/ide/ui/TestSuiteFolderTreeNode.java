package com.yukthitech.autox.ide.ui;

import java.io.File;
import java.io.IOException;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.FileTreeNode;
import com.yukthitech.autox.ide.projexplorer.FolderTreeNode;

public class TestSuiteFolderTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;

	private File testSuiteFolder;

	private Project project;

	public TestSuiteFolderTreeNode(Project project, String name, File testSuiteFolder)
	{
		super(IdeUtils.loadIcon("/ui/icons/settings.png", 20), name);

		this.project = project;
		this.testSuiteFolder = testSuiteFolder;
		reload();
	}
	
	@Override
	public synchronized void reload()
	{		
		File[] files = null;
		try
		{
			files = testSuiteFolder.getCanonicalFile().listFiles();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		// load all the files and folder of the prject directory
		for(File f : files)
		{
			if(f.isDirectory())
			{
				super.addChild(f.getName(), new FolderTreeNode(project, f.getName(), f));
			}
		}
		for(File f : files)
		{
			if(!f.isDirectory())
			{
				super.addChild(f.getName(), new FileTreeNode(project, f.getName(), f, null));
			}
		}

	}

	public Project getProject()
	{
		return project;
	}
}
