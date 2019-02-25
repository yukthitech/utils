package com.yukthitech.autox.ide.model;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class ProjectClassLoader extends URLClassLoader
{
	public ProjectClassLoader(Project project)
	{
		super(getProjectClassPath(project), null);
	}

	/**
	 * Gets the urls of classpath of system classloader.
	 * @return
	 */
	public static URL[] getProjectClassPath(Project project)
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		List<URL> resUrls = new ArrayList<>();
		
		if(CollectionUtils.isNotEmpty(project.getClassPathEntriesList()))
		{
			for(String entry : project.getClassPathEntriesList())
			{
				File file = new File(entry);
				
				try
				{
					resUrls.add(file.toURI().toURL());
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurrred while converting file into url: {}", file.getPath());
				}
			}
		}
		
		resUrls.addAll(Arrays.asList( ((URLClassLoader)cl).getURLs() ));
		
        return resUrls.toArray(new URL[0]);
	}
	
	public String toClassPath()
	{
		StringBuilder builder = new StringBuilder();
		URL urls[] = super.getURLs();
		
		for(URL url : urls)
		{
			File file = FileUtils.toFile(url);
			builder.append(file.getPath());
			builder.append(File.pathSeparator);
		}
		
		return builder.toString();
	}
}
