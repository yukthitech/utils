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

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

public class CopyrightAdder
{
	private static String liceseText;
	
	public static void main(String[] args) throws Exception
	{
		liceseText = FileUtils.readFileToString(new File("LICENSE_TO_APPLY.txt"), Charset.defaultCharset());
		
		String path = "E:\\Kranthi\\github\\utils";
		File folder = new File(path);
		
		if(!folder.isDirectory())
		{
			System.err.println("No directory exists with specified path: " + args[0]);
			System.exit(-1);
		}
		
		applyLicense(folder);
		System.out.println("Completed successfully...");
	}
	
	private static void applyLicense(File folder)
	{
		folder.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				if(pathname.isFile())
				{
					if(pathname.getName().endsWith(".java"))
					{
						applyLicenseToJavaFile(pathname);
					}
					
					return false;
				}
				
				if(pathname.isDirectory())
				{
					applyLicense(pathname);
				}
				
				return false;
			}
		});
	}
	
	private static void applyLicenseToJavaFile(File file)
	{
		try
		{
			String content = FileUtils.readFileToString(file, Charset.defaultCharset());
			
			int packIdx = content.indexOf("package ");
			
			if(packIdx < 0)
			{
				System.out.println("Skipping header: " + file.getPath());
				return;
			}

			//remove content before package
			content = content.substring(packIdx);
			content = liceseText + content;

			file.setWritable(true);
			FileUtils.writeStringToFile(file, content, Charset.defaultCharset());
			
			System.out.println("Added header: " + file.getPath());
		}catch(Exception ex)
		{
			throw new IllegalStateException(ex);
		}
	}
}
