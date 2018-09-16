package com.yukthitech.autox.ide.model;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Represents project and its details.
 * @author akiran
 */
public class Project
{
	private static Logger logger = LogManager.getLogger(Project.class);
	
	/**
	 * Name of the project file.
	 */
	public static final String PROJECT_FILE_NAME = "autox-project.json";

	private String name;
	private String projectFilePath;
	private String appConfigFilePath;
	private String appPropertyFilePath;
	private String testsuiteFolderPath;

	public Project()
	{
		name = "";
		appConfigFilePath = "appConfig.xml";
		appPropertyFilePath = "app.properties";
		testsuiteFolderPath = "testsuites";
	}

	public String getName()
	{
		return name;
	}

	public void setName(String projectName)
	{
		this.name = projectName;
	}
	
	@JsonIgnore
	public void setProjectFilePath(String projectFilePath)
	{
		File projectFile = new File(projectFilePath);
		
		if(!projectFile.getName().equals(PROJECT_FILE_NAME))
		{
			throw new InvalidArgumentException("Invalid project file specified: " + projectFilePath);
		}

		this.projectFilePath = projectFilePath;
	}
	
	public String getProjectFilePath()
	{
		return projectFilePath;
	}

	@JsonIgnore
	public String getBaseFolderPath()
	{
		return new File(projectFilePath).getParentFile().getPath();
	}

	public String getAppConfigFilePath()
	{
		return appConfigFilePath;
	}

	public void setAppConfigFilePath(String appConfigFilePath)
	{
		this.appConfigFilePath = appConfigFilePath;
	}

	public String getAppPropertyFilePath()
	{
		return appPropertyFilePath;
	}

	public void setAppPropertyFilePath(String appPropertyFilePath)
	{
		this.appPropertyFilePath = appPropertyFilePath;
	}

	public String getTestsuiteFolderPath()
	{
		return testsuiteFolderPath;
	}

	public void setTestsuiteFolderPath(String testsuiteFolderPath)
	{
		this.testsuiteFolderPath = testsuiteFolderPath;
	}

	/**
	 * Creates all the folder structure and files required by current project.
	 * @throws IOException
	 */
	public void createProject() throws IOException
	{
		File projFile = new File(projectFilePath);
		File baseFolder = projFile.getParentFile();
		
		if(!baseFolder.exists())
		{	
			baseFolder.mkdirs();
		}

		logger.trace("Creating project with path: {}",testsuiteFolderPath );
		String testsuitePath=testsuiteFolderPath.replace("./", baseFolder.getPath()+"/");
		
		File testSuiteFolder = new File(testsuitePath);
				
		if(!testSuiteFolder.exists())
		{
			testSuiteFolder.mkdirs();

		}
		
		String appPropPath = appPropertyFilePath.replace("./", baseFolder.getPath() + "/");
		
		File appProp = new File(appPropPath);
		
		if(!appProp.exists())
		{
			appProp.createNewFile();
		}
		
		String appConfFilePath = appConfigFilePath.replace("./", baseFolder.getPath()+"/");
		
		File appConfig = new File(appConfFilePath);
		
		if(!appConfig.exists())
		{
			appConfig.createNewFile();
		}
		
		save();
	}
	
	/**
	 * Loads the project from specified base folder path.
	 * @param path base folder path from which project needs to be loaded.
	 * @return loaded project
	 */
	public static Project load(String path)
	{
		File projectFile = new File(path);
		
		if(!projectFile.getName().equals(PROJECT_FILE_NAME))
		{
			throw new InvalidArgumentException("Invalid project file specified: " + path);
		}
		
		Project proj =  IdeUtils.load(projectFile, Project.class);
		
		if(proj == null)
		{
			return null;
		}
		
		proj.setProjectFilePath(path);
		return proj;
	}
	
	/**
	 * Saves the current project and its state.
	 */
	public void save()
	{
		IdeUtils.save(this, new File(projectFilePath));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof Project))
		{
			return false;
		}

		Project other = (Project) obj;
		return name.equals(other.name);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
