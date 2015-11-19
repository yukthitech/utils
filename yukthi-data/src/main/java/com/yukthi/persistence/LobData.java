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

package com.yukthi.persistence;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;

import com.yukthi.utils.exceptions.InvalidStateException;

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
