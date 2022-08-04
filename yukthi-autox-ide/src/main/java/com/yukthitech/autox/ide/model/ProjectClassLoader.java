package com.yukthitech.autox.ide.model;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class ProjectClassLoader extends URLClassLoader
{
	public ProjectClassLoader(Set<String> projectClassPath)
	{
		super(getProjectClassPath(projectClassPath), null);
	}

	/**
	 * Gets the urls of classpath of system classloader.
	 * @return
	 */
	public static URL[] getProjectClassPath(Set<String> projectClassPath)
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		List<URL> resUrls = new ArrayList<>();
		
		if(CollectionUtils.isNotEmpty(projectClassPath))
		{
			for(String entry : projectClassPath)
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
