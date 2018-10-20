package com.yukthitech.autox.ide.projexplorer;

import java.io.File;
import java.io.IOException;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;

public class ProjectTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;

	private Project project;

	public ProjectTreeNode(Project project)
	{
		super(IdeUtils.loadIcon("/ui/icons/project.png", 20), project.getName());

		this.project = project;
		File file = new File(project.getBaseFolderPath(), project.getTestsuiteFolderPath());
		String testsuite = project.getTestsuiteFolderPath();
		String s = testsuite.replace(project.getBaseFolderPath(), " ");
		super.addChild("testSuitesFolder", new TestSuiteFolderTreeNode(project, s, file));
		File[] files = null;
		try
		{
			files = new File(project.getBaseFolderPath()).getCanonicalFile().listFiles();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		// load all the files and folder of the prject directory
		for(File f : files)
		{
			if(f.isDirectory()&&!(f.getName().startsWith(".")))
			{
				super.addChild(f.getName(), new FolderTreeNode(project, f.getName(), f));
			}
		}
		for(File f : files)
		{
			if(!f.isDirectory() && !(f.getName().startsWith("."))&& !(f.getName().equals("autox-project.json")))
			{
					super.addChild(f.getName(), new FileTreeNode(project, f.getName(), f, null));
			}
		}

		// add app config file
		File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
		super.addChild("appConfig", new FileTreeNode(project, "App Configuration", appConfigFile, null));

		// add app prop file
		File appPropFile = new File(project.getBaseFolderPath(), project.getAppPropertyFilePath());
		super.addChild("appProp", new FileTreeNode(project, "App Properties", appPropFile, null));
	}

	@Override
	public void reload()
	{
		for(BaseTreeNode child : super.getChildNodes())
		{
			child.reload();
		}
	}

	public Project getProject()
	{
		return project;
	}
}
