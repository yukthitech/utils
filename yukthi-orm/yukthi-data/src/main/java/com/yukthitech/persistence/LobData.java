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
package com.yukthitech.persistence;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Expected only for internal use. Used to set blob/clob data (file) which can be used to 
 * save value in blob fields. 
 * @author akiran
 */
public class LobData implements Closeable
{
	/**
	 * File indicating the content
	 */
	private File file;

	private FileInputStream fis = null;
	
	private Reader reader = null;
	
	private boolean textStream;
	
	public LobData(File file, boolean textStream)
	{
		if(!file.exists())
		{
			throw new IllegalArgumentException("Specified file does not exist - " + file.getPath());
		}
		
		this.file = file;
		this.textStream = textStream;
	}
	
	/**
	 * @return the {@link #textStream textStream}
	 */
	public boolean isTextStream()
	{
		return textStream;
	}
	
	public Reader openReader()
	{
		if(reader != null)
		{
			return reader;
		}
		
		try
		{
			reader = new FileReader(file);
			return reader;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while opening file - {}", file.getPath());
		}
	}
	
	public InputStream openStream()
	{
		if(fis != null)
		{
			return fis;
		}
		
		try
		{
			fis = new FileInputStream(file);
			return fis;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while opening file - {}", file.getPath());
		}
	}
	
	@Override
	public void close()
	{
		if(fis != null)
		{
			try
			{
				fis.close();
				fis = null;
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while closing file stream - {}", file.getPath());
			}
		}
		
		if(reader != null)
		{
			try
			{
				reader.close();
				reader = null;
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while closing file reader - {}", file.getPath());
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("File: ").append(file.getPath());

		builder.append("]");
		return builder.toString();
	}

}
