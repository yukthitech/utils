package com.yukthitech.utils;

import java.io.File;
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
		FileUtils.write(file1, "Text for file1");
		
		File file2 = File.createTempFile("test", ".txt");
		FileUtils.write(file2, "Text for file2");
		
		File file3 = File.createTempFile("test", ".txt");
		FileUtils.write(file3, "Text for file3");
		
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
