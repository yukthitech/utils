package com.yukthitech.autox.ide.projexplorer;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;

public class ProjectTreeNode extends FolderTreeNode
{
	private static final long serialVersionUID = 1L;

	private Project project;
	
	private ProjectExplorer projectExplorer;

	public ProjectTreeNode(ProjectExplorer projectExplorer, Project project)
	{
		super(projectExplorer, IdeUtils.loadIcon("/ui/icons/project.png", 20), project, project.getName(), new File(project.getBaseFolderPath()));

		this.projectExplorer = projectExplorer;
		this.project = project;
		reload(false);
	}
	
	@Override
	protected boolean checkForRemoval(BaseTreeNode child)
	{
		if( super.checkForRemoval(child) )
		{
			return true;
		}
		
		if(child instanceof TestSuiteFolderTreeNode)
		{
			TestSuiteFolderTreeNode tsNode = (TestSuiteFolderTreeNode) child;
			
			if(project.getTestSuitesFoldersList() == null || !project.getTestSuitesFoldersList().contains(tsNode.getLabel()))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void reload(boolean childReload)
	{
		//project will be null, when reload is called by folder-tree-node constructor
		if(project == null)
		{
			return;
		}
		
		super.reload(childReload);
		
		for(BaseTreeNode child : super.getChildNodes())
		{
			if(childReload)
			{
				child.reload(true);
			}
		}

		int index = 0;
		Set<String> testSuitesFolders = project.getTestSuitesFoldersList();
		
		if(testSuitesFolders != null)
		{
			testSuitesFolders = new TreeSet<>( project.getTestSuitesFoldersList() );
			
			BaseTreeNode existingNode = null;
			
			for(String tsf : testSuitesFolders)
			{
				File file = new File(project.getBaseFolderPath(), tsf);
				String id = ":testSuite:" + tsf;
				existingNode = super.getChild(id);
				
				if(existingNode != null)
				{
					existingNode.reload(childReload);
				}
				else
				{
					super.insert(id, new TestSuiteFolderTreeNode(projectExplorer, project, tsf, file), index);
				}
				
				index++;
			}
		}
		
		// add app config file
		if(super.getChild(":appConfig") == null)
		{
			File appConfigFile = new File(project.getBaseFolderPath(), project.getAppConfigFilePath());
			super.insert(":appConfig", new FileTreeNode(projectExplorer, project, "App Configuration", appConfigFile, null), index);
		}

		index++;

		// add app prop file
		if(super.getChild(":appProp") == null)
		{
			File appPropFile = new File(project.getBaseFolderPath(), project.getAppPropertyFilePath());
			super.insert(":appProp", new FileTreeNode(projectExplorer, project, "App Properties", appPropFile, null), index);
		}
	}

	public Project getProject()
	{
		return project;
	}
}