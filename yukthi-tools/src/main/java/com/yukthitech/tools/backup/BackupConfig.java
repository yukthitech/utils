/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.tools.backup;

import java.util.ArrayList;
import java.util.List;

/**
 * Configurations related to backup.
 * @author akiran
 */
public class BackupConfig
{
	/**
	 * Entry of file for which backup needs to be maintained.
	 */
	public static class Entry
	{
		/**
		 * Name of the file to maintain.
		 */
		private String name;
		
		/**
		 * Local path of the file of which backup needs to be maintained.
		 */
		private String path;

		/**
		 * Gets the name of the file to maintain.
		 *
		 * @return the name of the file to maintain
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the file to maintain.
		 *
		 * @param name the new name of the file to maintain
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the local path of the file of which backup needs to be maintained.
		 *
		 * @return the local path of the file of which backup needs to be maintained
		 */
		public String getPath()
		{
			return path;
		}

		/**
		 * Sets the local path of the file of which backup needs to be maintained.
		 *
		 * @param path the new local path of the file of which backup needs to be maintained
		 */
		public void setPath(String path)
		{
			this.path = path;
		}
	}
	
	/**
	 * List of files to be managed.
	 */
	private List<Entry> files = new ArrayList<>();

	/**
	 * Duration of backup in minutes.
	 */
	private long durationMins;
	
	/**
	 * Number of versions to maintain.
	 */
	private int maxVersions = 7;
	
	/**
	 * Command to execute before taking backup.
	 */
	private String preExecuteCommand;

	/**
	 * Gets the list of files to be managed.
	 *
	 * @return the list of files to be managed
	 */
	public List<Entry> getFiles()
	{
		return files;
	}

	/**
	 * Sets the list of files to be managed.
	 *
	 * @param files the new list of files to be managed
	 */
	public void setFiles(List<Entry> files)
	{
		if(files == null)
		{
			throw new NullPointerException("Files can not be null.");
		}
		
		this.files = files;
	}
	
	/**
	 * Adds the file.
	 *
	 * @param file the file
	 */
	public void addFile(Entry file)
	{
		this.files.add(file);
	}

	/**
	 * Gets the duration of backup in minutes.
	 *
	 * @return the duration of backup in minutes
	 */
	public long getDurationMins()
	{
		return durationMins;
	}

	/**
	 * Sets the duration of backup in minutes.
	 *
	 * @param durationMins the new duration of backup in minutes
	 */
	public void setDurationMins(long durationMins)
	{
		this.durationMins = durationMins;
	}

	/**
	 * Gets the number of versions to maintain.
	 *
	 * @return the number of versions to maintain
	 */
	public int getMaxVersions()
	{
		return maxVersions;
	}

	/**
	 * Sets the number of versions to maintain.
	 *
	 * @param maxVersions the new number of versions to maintain
	 */
	public void setMaxVersions(int maxVersions)
	{
		this.maxVersions = maxVersions;
	}

	public String getPreExecuteCommand()
	{
		return preExecuteCommand;
	}

	public void setPreExecuteCommand(String preExecuteCommand)
	{
		this.preExecuteCommand = preExecuteCommand;
	}
}
