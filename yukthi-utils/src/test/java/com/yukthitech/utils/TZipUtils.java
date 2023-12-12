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
package com.yukthitech.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TZipUtils
{
	@Test
	public void testCreateZipFile() throws Exception
	{
		File file1 = File.createTempFile("test", ".txt");
		FileUtils.write(file1, "Text for file1", Charset.defaultCharset());
		
		File file2 = File.createTempFile("test", ".txt");
		FileUtils.write(file2, "Text for file2", Charset.defaultCharset());
		
		File file3 = File.createTempFile("test", ".txt");
		FileUtils.write(file3, "Text for file3", Charset.defaultCharset());
		
		Map<String, File> entries = new HashMap<String, File>();
		entries.put("dir1/file1.txt", file1);
		entries.put("dir/dir2/file2.txt", file2);
		entries.put("dir/dir3/file3.txt", file3);
		
		File zipFile = ZipUtils.zipFiles(entries);
		System.out.println(zipFile.getPath());
		
		FileUtils.deleteQuietly(file1);
		FileUtils.deleteQuietly(file2);
		FileUtils.deleteQuietly(file3);
		
		//unzip the create zip file
		File tempFolder = new File( file1.getParentFile(), "" + System.currentTimeMillis() );
		ZipUtils.unzip(zipFile, tempFolder);
		
		System.out.println("Root folder: " + tempFolder.getPath());
		Assert.assertTrue( new File(tempFolder, "dir1/file1.txt").exists() );
		Assert.assertTrue( new File(tempFolder, "dir/dir2/file2.txt").exists() );
		Assert.assertTrue( new File(tempFolder, "dir/dir3/file3.txt").exists() );
		
		FileUtils.deleteDirectory(tempFolder);
		FileUtils.deleteQuietly(zipFile);
	}
}
