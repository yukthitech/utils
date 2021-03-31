package com.yukthitech.utils;

import java.io.File;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class FileUtils
{
	public static String getCanonicalPath(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching canonical path: {}", file.getPath(), ex);
		}
	}

	public static String getRelativePath(File parent, File child)
	{
		String parentPath = getCanonicalPath(parent);
		String childPath = getCanonicalPath(child);
		
		if(parentPath.equals(childPath))
		{
			return "";
		}
		
		if(!childPath.startsWith(parentPath))
		{
			return null;
		}
		
		String relativePath = childPath.replace(parentPath, "");
		File relativePathFile = new File(parentPath, relativePath);
		
		relativePath = (childPath.equals(getCanonicalPath(relativePathFile))) ? relativePath : null;
		
		if(relativePath == null)
		{
			return null;
		}
		
		if(relativePath.startsWith(File.separator))
		{
			relativePath = relativePath.substring(1);
		}
		
		return relativePath;
	}

}
