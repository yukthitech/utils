package com.yukthitech.autox.ide.proj;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.model.ProjectState;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;

/**
 * Manager to manage projects.
 * @author akranthikiran
 */
@Service
public class ProjectManager
{
	private static Logger logger = LogManager.getLogger(ProjectManager.class);
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ProjectExplorer projectExplorer;

	private Set<Project> projects = new HashSet<Project>();

	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				state.retainProjects(projects);
			}
			
			@Override
			public void loadState(IdeState state)
			{
				for(ProjectState project : state.getOpenProjects())
				{
					openProject(project.getPath());
				}
				
				projectExplorer.loadFilesToIndex();
			}
		});
	}
	
	/**
	 * Opens the project from specified base folder path.
	 * @param path base folder path of project to open
	 */
	public Project openProject(String path)
	{
		logger.debug("Loading project at path: {}", path);
		
		Project project = Project.load(path);
		
		if(project == null)
		{
			logger.debug("Failed to load project from path: " + path);
			return null;
		}
		
		if(projects.contains(project))
		{
			return project;
		}
		
		projectExplorer.openProject(project);
		projects.add(project);
		return project;
	}

	public void deleteProject(Project project, boolean deleteContent)
	{
		if(!projects.contains(project))
		{
			logger.debug("Specified project is not found in open list. Ignoring project delete request: {}", project.getName());
			return;
		}
		
		projectExplorer.deleteProject(project);
		projects.remove(project);
		
		if(deleteContent)
		{
			try
			{
				project.deleteProjectContents();
			}catch(Exception ex)
			{
				JOptionPane.showMessageDialog(projectExplorer, "Failed to delete project '" + project.getName() +"'. \nError: " + ex);
			}
		}
		
		ideContext.getProxy().projectRemoved(project);
	}
	
	public Project getProject(String name)
	{
		Project res = projects.stream()
				.filter(proj -> name.equals(proj.getName()))
				.findFirst()
				.orElse(null);
		
		return res;
	}
	
	public void updateProject(Project project)
	{
		project.save();

		//as the project name might have changed, remove existing one
		//  by reference and readd modified project
		this.projects.removeIf(proj -> (proj == project));
		this.projects.add(project);
		

		ideContext.getProxy().projectStateChanged(project);
	}
}
