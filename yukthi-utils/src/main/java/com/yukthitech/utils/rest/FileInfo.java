/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
	 * @param file the file
	 * @param contentType the content type
	 */
	public FileInfo(File file, String contentType)
	{
		this.file = file;
		this.contentType = contentType;
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
