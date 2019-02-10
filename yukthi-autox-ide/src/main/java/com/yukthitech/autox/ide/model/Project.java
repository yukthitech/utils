package com.yukthitech.autox.ide.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.beans.BeanPropertyInfoFactory;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents project and its details.
 * 
 * @author akiran
 */
public class Project implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(Project.class);

	/**
	 * Name of the project file.
	 */
	public static final String PROJECT_FILE_NAME = "autox-project.json";

	private String name;
	private String projectFilePath;
	private String appConfigFilePath;
	private String appPropertyFilePath;
	private Set<String> testSuitesFoldersList;
	private Set<String> classPathEntriesList;
	
	private transient ProjectClassLoader projectClassLoader;
	
	private transient DocInformation docInformation;
	
	private transient BeanPropertyInfoFactory beanPropertyInfoFactory;
	
	private transient File baseFolder;
	
	private transient Set<File> reservedFiles;
	
	public Project()
	{
		name = "";
		appConfigFilePath = "appConfig.xml";
		appPropertyFilePath = "app.properties";
		testSuitesFoldersList = CommonUtils.toSet("src/testsuites");
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
		if(projectFilePath == null)
		{
			return null;
		}

		try
		{
			return new File(projectFilePath).getParentFile().getCanonicalPath();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while loading project file cannonical path: " + projectFilePath, ex);
		}
	}
	
	@JsonIgnore
	public File getBaseFolder()
	{
		if(baseFolder != null)
		{
			return baseFolder;
		}
		
		baseFolder = new File(projectFilePath).getParentFile();
		return baseFolder;
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

	public Set<String> getTestSuitesFoldersList()
	{
		return testSuitesFoldersList;
	}

	public void setTestSuitesFolders(Set<String> testSuitesFoldersList)
	{
		this.testSuitesFoldersList = testSuitesFoldersList;
	}
	
	public void addTestSuiteFolder(String folder)
	{
		if(this.testSuitesFoldersList == null)
		{
			this.testSuitesFoldersList = new HashSet<>();
		}
		
		this.testSuitesFoldersList.add(folder);
	}

	public Set<String> getClassPathEntriesList()
	{
		String libJarPath = null;
		File libFolder = new File(baseFolder, "lib");
		
		try
		{
			libJarPath = libFolder.getCanonicalPath() + File.separator + "*";
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while getting canonical path of lib folder: " + libFolder.getPath(), ex);
		}
		
		if(classPathEntriesList == null)
		{
			return Collections.singleton(libJarPath);
		}
		else if(!classPathEntriesList.contains(libJarPath))
		{
			classPathEntriesList.add(libJarPath);
		}
		
		return classPathEntriesList;
	}

	public void setClassPathEntries(Set<String> classPathEntriesList)
	{
		this.classPathEntriesList = classPathEntriesList;
	}

	/**
	 * Creates all the folder structure and files required by current project.
	 * 
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

		logger.trace("Creating project with path: {}", testSuitesFoldersList);
		
		for(String testsuiteFolderPath : testSuitesFoldersList)
		{
			String testsuitePath = testsuiteFolderPath.replace("./", baseFolder.getPath() + "/");
	
			File testSuiteFolder = new File(testsuitePath);
	
			if(!testSuiteFolder.exists())
			{
				testSuiteFolder.mkdirs();
			}
		}

		String appPropPath = appPropertyFilePath.replace("./", baseFolder.getPath() + "/");

		File appProp = new File(appPropPath);

		if(!appProp.exists())
		{
			appProp.createNewFile();
		}

		String appConfFilePath = appConfigFilePath.replace("./", baseFolder.getPath() + "/");

		File appConfig = new File(appConfFilePath);

		if(!appConfig.exists())
		{
			appConfig.createNewFile();
		}

		save();
	}

	/**
	 * Loads the project from specified base folder path.
	 * 
	 * @param path
	 *            base folder path from which project needs to be loaded.
	 * @return loaded project
	 */
	public static Project load(String path)
	{
		File projectFile = new File(path);

		if(!projectFile.getName().equals(PROJECT_FILE_NAME))
		{
			throw new InvalidArgumentException("Invalid project file specified: " + path);
		}

		Project proj = IdeUtils.load(projectFile, Project.class);

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
	
	@JsonIgnore
	public ProjectClassLoader getProjectClassLoader()
	{
		if(projectClassLoader == null)
		{
			projectClassLoader = new ProjectClassLoader(this);
		}
		
		return projectClassLoader;
	}
	
	@JsonIgnore
	public DocInformation getDocInformation()
	{
		if(docInformation != null)
		{
			return docInformation;
		}

		String[] basepackage = { "com.yukthitech" };

		try
		{
			docInformation = DocGenerator.buildDocInformation(basepackage);
		} catch(Exception e)
		{
			throw new IllegalStateException("An error occured while loading document Information", e);
		}
		
		return docInformation;
	}
	
	@JsonIgnore
	public BeanPropertyInfoFactory getBeanPropertyInfoFactory()
	{
		if(beanPropertyInfoFactory != null)
		{
			return beanPropertyInfoFactory;
		}
		
		beanPropertyInfoFactory = new BeanPropertyInfoFactory();
		return beanPropertyInfoFactory;
	}
	
	@JsonIgnore
	public synchronized boolean isReservedFile(File file)
	{
		if(file.getName().startsWith("."))
		{
			return true;
		}
		
		if(reservedFiles == null)
		{
			reservedFiles = new HashSet<>();
			File baseFolder = getBaseFolder();
			
			try
			{
				//exclude maven target folder
				reservedFiles.add( new File(baseFolder, "target").getCanonicalFile() );
				
				//exclude autox files
				reservedFiles.add( new File(baseFolder, appConfigFilePath).getCanonicalFile() );
				reservedFiles.add( new File(baseFolder, appPropertyFilePath).getCanonicalFile() );
				reservedFiles.add( new File(projectFilePath).getCanonicalFile() );
				
				for(String testSuiteFolder : this.testSuitesFoldersList)
				{
					reservedFiles.add( new File(baseFolder, testSuiteFolder).getCanonicalFile() );
				}
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while creating reserved folder list", ex);
			}
		}
		
		return reservedFiles.contains(file);
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
