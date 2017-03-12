package com.yukthitech.automation.test;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manages report resources and copies to output folder.
 * @author akiran
 */
public class ReportResourceManager
{
	/**
	 * Classpath resource folder where rsource files are maintained.
	 */
	private static final String REPORT_RES_FOLDER = "/report-resources/";
	
	private static final ReportResourceManager instance = new ReportResourceManager();
	
	static
	{
		try
		{
			XMLBeanParser.parse(ReportResourceManager.class.getResourceAsStream("/report-resource-list.xml"), instance);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading report resource list", ex);
		}
	}
	
	/**
	 * Represents resource file.
	 * @author akiran
	 */
	public static class ResourceFile
	{
		/**
		 * Path of the resource file in report resources folder.
		 */
		private String path;
		
		public void setPath(String path)
		{
			this.path = path;
		}
	}
	
	/**
	 * Resource files to be copied with generated report data.
	 */
	private List<ResourceFile> resourceFiles = new ArrayList<>();
	
	/**
	 * Gets singleton instance of this class.
	 * @return
	 */
	public static ReportResourceManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Adds a file to be copied.
	 * @param file
	 */
	public void addFile(ResourceFile file)
	{
		this.resourceFiles.add(file);
	}
	
	public void copyResources(File outFolder)
	{
		String subpath = null;
		int idx = 0;
		File parentFolder = null, outFile = null;
		
		for(ResourceFile file : resourceFiles)
		{
			idx = file.path.lastIndexOf("/");
			parentFolder = outFolder;
			
			//if required create sub folders
			if(idx > 0)
			{
				subpath = file.path.substring(0, idx);
				parentFolder = new File(outFolder, subpath);
				
				if(!parentFolder.exists())
				{
					try
					{
						FileUtils.forceMkdir(parentFolder);
					}catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while creating output folder path: {}", parentFolder.getPath());
					}
				}
				
				outFile = new File(parentFolder, file.path.substring(idx + 1));
			}
			else
			{
				outFile = new File(parentFolder, file.path);
			}
			
			try
			{
				InputStream is = ReportResourceManager.class.getResourceAsStream(REPORT_RES_FOLDER + file.path);
				FileUtils.copyInputStreamToFile(is, outFile);
				is.close();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while copying resource '{}' as out file: {}", REPORT_RES_FOLDER + file.path, outFile.getPath(), ex);
			}
		}
	}
}
