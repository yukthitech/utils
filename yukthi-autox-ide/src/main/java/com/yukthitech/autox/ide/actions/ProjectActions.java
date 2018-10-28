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
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.projpropdialog.ProjectPropertiesDialog;

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

	private ProjectPropertiesDialog projtPropertiesDialog;

	@PostConstruct
	private void init()
	{
		newProjDialog = new NewProjectDialog(IdeUtils.getCurrentWindow());

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
		projectExplorer.openProject(project.getProjectFilePath());
	}

	@Action
	public void openProject()
	{
		if(projectChooser.showOpenDialog(IdeUtils.getCurrentWindow()) == JFileChooser.APPROVE_OPTION)
		{
			projectExplorer.openProject(projectChooser.getSelectedFile().getPath());
		}
	}

	@Action
	public void deleteProject()
	{

	}

	@Action
	public void refreshProject()
	{
		projectExplorer.reloadActiveNode();
	}

	@Action
	public void projectProperties()
	{
		if(projtPropertiesDialog == null)
		{
			projtPropertiesDialog = new ProjectPropertiesDialog(IdeUtils.getCurrentWindow());
			IdeUtils.autowireBean(applicationContext, projtPropertiesDialog);
		}

		projtPropertiesDialog.display(ideContext);
	}
}
