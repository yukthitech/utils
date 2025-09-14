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

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Expected only for internal use. Used to set blob/clob data (file) which can be used to 
 * save value in blob fields. 
 * @author akiran
 */
public class LobData implements Closeable
{
	private InputStream is = null;
	
	private Reader reader = null;
	
	private boolean closed;
	
	public LobData(File file, boolean textStream)
	{
		if(!file.exists())
		{
			throw new IllegalArgumentException("Specified file does not exist - " + file.getPath());
		}

		try
		{
			if(textStream)
			{
				reader = new FileReader(file);
			}
			else
			{
				is = new FileInputStream(file);
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to open file: {}", file.getPath(), ex);
		}
	}
	
	public LobData(byte[] data)
	{
		this.is = new ByteArrayInputStream(data);
	}
	
	public LobData(char[] data)
	{
		this.reader = new StringReader(new String(data));
	}
	
	public LobData(String data)
	{
		this.reader = new StringReader(data);
	}

	/**
	 * @return the {@link #textStream textStream}
	 */
	public boolean isTextStream()
	{
		return (reader != null);
	}
	
	public Reader openReader()
	{
		if(closed)
		{
			throw new InvalidStateException("Lob data is already closed");
		}
		
		return reader;
	}
	
	public InputStream openStream()
	{
		if(closed)
		{
			throw new InvalidStateException("Lob data is already closed");
		}
		
		return is;
	}
	
	@Override
	public void close()
	{
		closed = true;
		
		if(is != null)
		{
			try
			{
				is.close();
				is = null;
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while closing stream", ex);
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
				throw new InvalidStateException("An error occurred while closing reader", ex);
			}
		}
		
	}
}
