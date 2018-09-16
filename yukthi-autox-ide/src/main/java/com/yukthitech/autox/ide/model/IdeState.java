package com.yukthitech.autox.ide.model;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.yukthitech.autox.ide.IdeUtils;

/**
 * Maintains the state of ide.
 * @author akiran
 */
public class IdeState implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * File in which ide state will be persisted.
	 */
	private static final File IDE_STATE_FILE = new File("autox-ide.state");
	
	/**
	 * List of project currently open.
	 */
	private Set<ProjectState> openProjects = new LinkedHashSet<>();
	
	/**
	 * Custom attributes.
	 */
	private Map<String, Object> attributes = new HashMap<>();
	
	/**
	 * Used for internal indexing.
	 */
	private transient Map<String, ProjectState> projectIndex = new HashMap<>();

	/**
	 * Gets the list of project currently open.
	 *
	 * @return the list of project currently open
	 */
	public Set<ProjectState> getOpenProjects()
	{
		return openProjects;
	}

	/**
	 * Sets the list of project currently open.
	 *
	 * @param openProjects the new list of project currently open
	 */
	public void setOpenProjects(Set<ProjectState> openProjects)
	{
		this.openProjects.addAll(openProjects);
	}
	
	/**
	 * Adds open project to current state.
	 * @param project
	 */
	public ProjectState addOpenProject(Project project)
	{
		ProjectState state = projectIndex.get(project.getName());
		
		if(state != null)
		{
			return state;
		}
		
		state = new ProjectState(project.getProjectFilePath());
		projectIndex.put(project.getName(), state);
		this.openProjects.add(state);
		return state;
	}
	
	/**
	 * Sets the attribute with specified name and value.
	 * @param name
	 * @param value
	 */
	public void setAtribute(String name, Object value)
	{
		this.attributes.put(name, value);
	}
	
	/**
	 * Gets attributed with specified name.
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	/**
	 * Saves the current state of ide.
	 */
	public void save()
	{
		IdeUtils.serialize(this, IDE_STATE_FILE);
	}
	
	/**
	 * Loads the ide state.
	 * @return
	 */
	public static IdeState load()
	{
		IdeState savedState = (IdeState) IdeUtils.deserialize(IDE_STATE_FILE);
		return savedState;
	}
}
