package com.yukthitech.autox.ide.actions;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.NewProjectDialog;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.dialog.DeleteProjectDialog;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.projexplorer.ProjectTreeNode;
import com.yukthitech.autox.ide.projpropdialog.ProjectPropertiesDialog;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.InProgressDialog;

@ActionHolder
public class ProjectActions
{
	private JFileChooser projectChooser = new JFileChooser();

	@Autowired
	private IdeContext ideContext;

	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private ApplicationContext applicationContext;

	private NewProjectDialog newProjDialog;

	private ProjectPropertiesDialog projPropertiesDialog;
	
	private DeleteProjectDialog deleteProjectDialog;

	@PostConstruct
	private void init()
	{
		newProjDialog = new NewProjectDialog(IdeUtils.getCurrentWindow());
		deleteProjectDialog = new DeleteProjectDialog(projectExplorer);

		projectChooser.setDialogTitle("Open Project");
		projectChooser.setAcceptAllFileFilterUsed(false);
		projectChooser.setFileFilter(new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return "AutoX Project Files (" + Project.PROJECT_FILE_NAME + ")";
			}

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory() || Project.PROJECT_FILE_NAME.equals(f.getName());
			}
		});
	}

	public Project openExistingProject(String path)
	{
		return projectExplorer.openProject(path);
	}

	@Action
	public void newProject()
	{
		Project project = newProjDialog.display();
		
		if(project != null)
		{
			InProgressDialog.getInstance().display("Opening new project. Please wait...", new Runnable()
			{
				@Override
				public void run()
				{
					projectExplorer.openProject(project.getProjectFilePath());
				}
			});
		}
	}

	@Action
	public void openProject()
	{
		if(projectChooser.showOpenDialog(IdeUtils.getCurrentWindow()) == JFileChooser.APPROVE_OPTION)
		{
			InProgressDialog.getInstance().display("Opening project. Please wait...", new Runnable()
			{
				@Override
				public void run()
				{
					projectExplorer.openProject(projectChooser.getSelectedFile().getPath());					
				}
			});
		}
	}

	@Action
	public void deleteProject()
	{
		BaseTreeNode selectedNode = projectExplorer.getActiveNode();
		
		if(!(selectedNode instanceof ProjectTreeNode))
		{
			return;
		}
		
		Project proj = ((ProjectTreeNode) selectedNode).getProject();
		deleteProjectDialog.displayFor(proj);
	}

	@Action
	public void refreshProject()
	{
		BaseTreeNode node = projectExplorer.getActiveNode();
		
		if(node instanceof ProjectTreeNode)
		{
			ProjectTreeNode projNode = (ProjectTreeNode) node;
			projNode.getProject().reset();
		}

		projectExplorer.reloadActiveNode();
	}

	@Action
	public void projectProperties()
	{
		if(projPropertiesDialog == null)
		{
			projPropertiesDialog = new ProjectPropertiesDialog(IdeUtils.getCurrentWindow());
			IdeUtils.autowireBean(applicationContext, projPropertiesDialog);
		}

		projPropertiesDialog.display(ideContext);
	}
}
