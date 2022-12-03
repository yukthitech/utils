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
package com.yukthitech.utils.rest;

import java.io.File;

/**
 * Information about the file to upload
 * @author akiran
 */
public class FileInfo
{
	/**
	 * Name of the attchment file to set.
	 */
	private String name;
	
	/**
	 * File to upload
	 */
	private File file;
	
	/**
	 * Content type of the file
	 */
	private String contentType;
	
	/**
	 * Instantiates a new file info.
	 */
	public FileInfo()
	{}
	
	/**
	 * Instantiates a new file info.
	 *
	 * @param name the name
	 * @param file the file
	 * @param contentType the content type
	 */
	public FileInfo(String name, File file, String contentType)
	{
		this.name = name;
		this.file = file;
		this.contentType = contentType;
	}
	
	/**
	 * Gets the name of the attchment file to set.
	 *
	 * @return the name of the attchment file to set
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the attchment file to set.
	 *
	 * @param name the new name of the attchment file to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the file to upload.
	 *
	 * @return the file to upload
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Sets the file to upload.
	 *
	 * @param file the new file to upload
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 * Gets the content type of the file.
	 *
	 * @return the content type of the file
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Sets the content type of the file.
	 *
	 * @param contentType the new content type of the file
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

}
