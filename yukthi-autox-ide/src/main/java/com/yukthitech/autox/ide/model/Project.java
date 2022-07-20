package com.yukthitech.autox.ide.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yukthitech.autox.AutoxVersion;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.proj.ProjectElementTracker;
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
	private String resourcesFolder;
	
	private transient ProjectClassLoader projectClassLoader;
	
	private transient DocInformation docInformation;
	
	private transient BeanPropertyInfoFactory beanPropertyInfoFactory;
	
	private transient File baseFolder;
	
	private transient Set<File> reservedFiles;
	
	private transient Set<String> finalClassPathEntries = null; 
	
	private transient ProjectElementTracker projectElementTracker;
	
	public Project()
	{
		name = "";
		appConfigFilePath = "src/main/config/app-configuration.xml";
		appPropertyFilePath = "src/main/config/app.properties";
		testSuitesFoldersList = CommonUtils.toSet("src/main/test-suites");
		resourcesFolder = "src/main/resources";
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
	
	public boolean isTestSuiteFolderFile(File file)
	{
		String baseFolderPath = getBaseFolderPath();
				
		for(String path : testSuitesFoldersList)
		{
			File folder = new File(baseFolderPath, path);
			
			if(IdeFileUtils.getRelativePath(folder, file) != null)
			{
				return true;
			}
		}
		
		return false;
	}

	public boolean isTestSuiteFolder(File file)
	{
		String relPath = IdeFileUtils.getRelativePath(getBaseFolder(), file);
		return testSuitesFoldersList.contains(relPath);
	}

	public void setTestSuiteFolders(Set<String> testSuitesFoldersList)
	{
		this.testSuitesFoldersList = new HashSet<String>(testSuitesFoldersList);
	}
	
	public void addTestSuiteFolder(String folder)
	{
		if(this.testSuitesFoldersList == null)
		{
			this.testSuitesFoldersList = new HashSet<>();
		}
		
		this.testSuitesFoldersList.add(folder);
	}

	@JsonIgnore
	public Set<String> getFullClassPathEntriesList()
	{
		if(finalClassPathEntries == null)
		{
			addDefaultClasspathEntries();
		}
		
		return finalClassPathEntries;
	}
	
	private synchronized void addDefaultClasspathEntries()
	{
		finalClassPathEntries = new LinkedHashSet<>();

		//add main resources and config folders
		finalClassPathEntries.add(new File(baseFolder, "src" + File.separator + "main" + File.separator + "config").getPath());
		finalClassPathEntries.add(new File(baseFolder, "src" + File.separator + "main" + File.separator + "resources").getPath());
		finalClassPathEntries.add(new File(baseFolder, "target" + File.separator + "classes").getPath());

		//add lib folder of project
		File libFolder = new File(baseFolder, "lib");
		
		if(libFolder.exists())
		{
			for(File file : libFolder.listFiles())
			{
				if(!file.getName().toLowerCase().endsWith(".jar") && !file.getName().toLowerCase().endsWith(".zip"))
				{
					continue;
				}
				
				finalClassPathEntries.add(file.getPath());
			}
		}
		
		String libJarPath = null;
		
		try
		{
			libJarPath = libFolder.getCanonicalPath() + File.separator + "*";
			finalClassPathEntries.add(libJarPath);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while getting canonical path of lib folder: " + libFolder.getPath(), ex);
		}
		
		if(classPathEntriesList != null)
		{
			finalClassPathEntries.addAll(classPathEntriesList);
		}
	}
	
	/**
	 * Sets the class path entries list.
	 * Added for backward compatibility.
	 *
	 * @param classPathEntriesList the new class path entries list
	 */
	public void setClassPathEntriesList(Set<String> classPathEntriesList)
	{
		this.setClassPathEntries(classPathEntriesList);
	}

	public void setClassPathEntries(Set<String> classPathEntriesList)
	{
		this.classPathEntriesList = classPathEntriesList;
		finalClassPathEntries = null;
	}
	
	public Set<String> getClassPathEntries()
	{
		return classPathEntriesList;
	}
	
	public void reset()
	{
		finalClassPathEntries = null;
		projectClassLoader = null;
		projectElementTracker = null;
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
			FileUtils.forceMkdir(baseFolder);
		}

		logger.trace("Creating project with path: {}", testSuitesFoldersList);
		
		for(String testsuiteFolderPath : testSuitesFoldersList)
		{
			File testSuiteFolder = new File(baseFolder, testsuiteFolderPath);
	
			if(!testSuiteFolder.exists())
			{
				FileUtils.forceMkdir(testSuiteFolder);
			}
		}
		
		File resourcesFolder = new File(baseFolder, this.resourcesFolder);

		if(!resourcesFolder.exists())
		{
			FileUtils.forceMkdir(resourcesFolder);
		}

		Map<String, String> pathToTemp = CommonUtils.toMap(
				appPropertyFilePath, "/templates/new-project/app-prop-template.properties",
				appConfigFilePath, "/templates/new-project/app-config-template.xml",
				"/pom.xml", "/templates/new-project/pom.xml",
				"/zip-pom.xml", "/templates/new-project/zip-pom.xml"
			);
		
		Map<String, String> context = CommonUtils.toMap(
				"testSuitesFolder", testSuitesFoldersList.iterator().next(),
				"projectName", this.name,
				"autoxVersion", AutoxVersion.getVersion()
			);
		
		for(Map.Entry<String, String> entry : pathToTemp.entrySet())
		{
			String path = entry.getKey();
			String temp = entry.getValue();
			
			File entryFile = new File(baseFolder, path);
			
			if(entryFile.getParentFile().exists())
			{
				FileUtils.forceMkdir(entryFile.getParentFile());
			}

			if(!entryFile.exists())
			{
				createFile(entryFile, temp, context);
				entryFile.createNewFile();
			}
		}

		save();
	}
	
	private void createFile(File file, String templateFile, Map<String, String> context) throws IOException
	{
		InputStream is = Project.class.getResourceAsStream(templateFile);
		String content = IOUtils.toString(is, Charset.defaultCharset());
		is.close();
		
		content = FreeMarkerMethodManager.replaceExpressions(templateFile, context, content);
		
		FileUtils.write(file, content, Charset.defaultCharset());
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
	
	public void deleteProjectContents() throws IOException
	{
		logger.debug("Deleting project contents from base folder: {}", baseFolder);
		FileUtils.forceDelete(baseFolder);
	}
	
	@JsonIgnore
	public synchronized ProjectElementTracker getProjectElementTracker()
	{
		if(projectElementTracker == null)
		{
			projectElementTracker = new ProjectElementTracker(this);
		}
		
		return projectElementTracker;
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
	
	public static void main(String[] args) throws Exception
	{
		Project project = new Project();
		project.setName("test");
		project.setProjectFilePath("C:\\Users\\akiran\\Documents\\Sound recordings\\test\\" + PROJECT_FILE_NAME);
		
		project.createProject();
	}
}
