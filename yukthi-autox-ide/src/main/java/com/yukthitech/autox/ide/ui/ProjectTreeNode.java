package com.yukthitech.autox.ide.ui;

import java.io.File;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;

public class ProjectTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;
	
	private Project project;

	public ProjectTreeNode(Project project)
	{
		super(IdeUtils.loadIcon("/ui/icons/project.png", 20), project.getName());
		
		this.project = project;
		super.addChild("testSuitesFolder", new FolderTreeNode(project, "Test Suites", new File(project.getBaseFolderPath(), project.getTestsuiteFolderPath()) ));
		
		//add app config file
		File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
		super.addChild("appConfig", new FileTreeNode(project, "App Configuration",  appConfigFile, null));
		
		//add app prop file
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
