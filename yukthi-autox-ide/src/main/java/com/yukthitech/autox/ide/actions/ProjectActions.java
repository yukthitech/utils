package com.yukthitech.autox.ide.actions;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.NewProjectDialog;
import com.yukthitech.autox.ide.ProjectExplorer;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;

@ActionHolder
public class ProjectActions
{
	private JFileChooser projectChooser = new JFileChooser();
	
	@Autowired
	private ProjectExplorer projectExplorer;
	
	private NewProjectDialog newProjDialog;
	
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
}
