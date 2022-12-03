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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Utility methods related to zipping and unzipping.
 * @author akiran
 */
public class ZipUtils
{
	/**
	 * Copies the data from specified input stream to specified output stream in zipped format.
	 * @param is input from which data to be copied
	 * @param os output to which zipped data will be copied
	 */
	public static void zipStream(InputStream is, OutputStream os)
	{
		GZIPOutputStream gzipOutputStream = null;
		
		try
		{
			gzipOutputStream = new GZIPOutputStream(os);
			IOUtils.copy(is, gzipOutputStream);
			
			gzipOutputStream.finish();
			gzipOutputStream.flush();
			os.flush();
			
			gzipOutputStream.close();
		} catch(IOException ex)
		{
			throw new IllegalStateException("Error while Zipping contents: " + ex.getMessage(), ex);
		}
	}
	
	/**
	 * Unzips the content from input zip and writes to specified output stream.
	 * @param zipInput input content to be unzipped.
	 * @param os output to which unzipped data needs to be written.
	 */
	public static void unzipStream(InputStream zipInput, OutputStream os)
	{
		GZIPInputStream gzipInputStream = null;
		
		try
		{
			gzipInputStream = new GZIPInputStream(zipInput);
			IOUtils.copy(gzipInputStream, os);
			
			os.flush();
		} catch(IOException ex)
		{
			throw new IllegalStateException("Error while Zipping contents: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Creates a temp file with zipped content of specified file and returns the same.
	 * @param file file to zip
	 * @return temp file which is populated with input zip content.
	 */
	public static File zipFile(File file)
	{
		try
		{
			File tempFile = File.createTempFile(file.getName(), ".zip");
			
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(tempFile);
			
			zipStream(fis, fos);
			
			fis.close();
			fos.close();
			
			return tempFile;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating zip of input file: " + file.getPath(), ex);
		}
	}
	
	/**
	 * Creates a temp file with unzipped content of specified file and returns the same.
	 * @param file file to unzip
	 * @return temp file which is populated with input unzip content.
	 */
	public static File unzipFile(File file)
	{
		try
		{
			File tempFile = File.createTempFile(file.getName(), ".tmp");
			
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(tempFile);
			
			unzipStream(fis, fos);
			
			fis.close();
			fos.close();
			
			return tempFile;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating zip of input file: " + file.getPath(), ex);
		}
	}
	
	/**
	 * Zips the input bytes and returns the zipped bytes.
	 * @param data data to zip.
	 * @return zipped bytes
	 */
	public static byte[] zipBytes(byte data[])
	{
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			zipStream(bis, bos);
			
			return bos.toByteArray();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating zip of input bytes", ex);
		}
	}

	/**
	 * Unzip specified zip bytes.
	 * @param zipData data to unzip
	 * @return unzipped bytes
	 */
	public static byte[] unzipBytes(byte zipData[])
	{
		try
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(zipData);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			unzipStream(bis, bos);
			
			return bos.toByteArray();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating zip of input bytes", ex);
		}
	}

	/**
	 * Creates a zip file of the specified file and returns the same. Input map should have expected file name as 
	 * key and the file as value (from which content will be fetched).
	 * @param files mapping from expected file name to file
	 * @return temp resultant zip file.
	 */
	public static File zipFiles(Map<String, File> files)
	{
		File zipFile = null;
		
		try
		{
			zipFile = File.createTempFile("temp", ".zip");
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating temp file", ex);
		}
		
		zipFiles(files, zipFile);
		
		return zipFile; 
	}
	
	/**
	 * Creates a zip file of the specified files and returns the same. Input map should have expected file name as 
	 * key and the file as value (from which content will be fetched).
	 * @param files mapping from expected file name to file
	 * @param zipFile output zip file
	 */
	public static void zipFiles(Map<String, File> files, File zipFile)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			for(String fileName : files.keySet())
			{
				ZipEntry zipEntry = new ZipEntry(fileName);
				zos.putNextEntry(zipEntry);
				
				File file = files.get(fileName);
				
				if(file != null)
				{
					FileInputStream input = new FileInputStream(file);
					IOUtils.copy(input, zos);
					input.close();
				}
				
				zos.closeEntry();
			}
			
			zos.close();
			fos.close();
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while zipping specified files", ex);
		}
	}
	
	/**
	 * Fetches all the files into specified file-map using relative path as the key.
	 * @param directory dir from which files needs to be fetched
	 * @param files map where files should be collected
	 * @param path current directory relative path
	 */
	private static void fetchFiles(File directory, final Map<String, File> files, final String path)
	{
		if(!path.isEmpty())
		{
			files.put(path, null);
		}
		
		directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				if(pathname.isFile())
				{
					files.put(path + pathname.getName(), pathname);
				}
				else
				{
					fetchFiles(pathname, files, path + pathname.getName() + "/");
				}
				
				return false;
			}
		});
	}
	
	/**
	 * Zips the specified directory into specified zip file.
	 * @param directory directory to be zipped.
	 * @param zipFile zip file to be created.
	 */
	public static void zipDirectory(File directory, File zipFile)
	{
		if(!directory.isDirectory())
		{
			throw new InvalidArgumentException("Invalid directory specified: {}", directory);
		}
		
		Map<String, File> files = new TreeMap<String, File>();
		fetchFiles(directory, files, "");
		
		zipFiles(files, zipFile);
	}
	
	/**
	 * Fetches zip entries from the specified file.
	 *
	 * @param zipFile the zip file to be read
	 * @return the zip entries
	 */
	public static Set<String> getZipEntries(File zipFile)
	{
		try
		{
			ZipFile zfile = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> it = zfile.entries();
			
			ZipEntry zipEntry = null;
			Set<String> zipEntries = new LinkedHashSet<String>();
			
			while(it.hasMoreElements())
			{
				zipEntry = it.nextElement();
				zipEntries.add(zipEntry.getName());
			}

			zfile.close();
			return zipEntries;
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while fetching zip file entries: " + zipFile, ex);
		}
	}
	
	/**
	 * Unzips the specified zip file to specified root folder.
	 * @param zipFile file to unzip file.
	 * @param rootFolder folder to which unzip should be done.
	 * @return Retuns the list of files unzipped.
	 */
	public static List<File> unzip(File zipFile, File rootFolder)
	{
		try
		{
			ZipFile zfile = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> it = zfile.entries();
			
			ZipEntry zipEntry = null;
			List<File> unzipFiles = new LinkedList<File>();
			
			while(it.hasMoreElements())
			{
				zipEntry = it.nextElement();
				File entryFile = new File(rootFolder, zipEntry.getName().replace("/", File.separator));
				
				if(zipEntry.isDirectory())
				{
					FileUtils.forceMkdir(entryFile);
					continue;
				}
				
				FileUtils.forceMkdir(entryFile.getParentFile());
				
				InputStream entryStream = zfile.getInputStream(zipEntry);
				FileUtils.copyInputStreamToFile(entryStream, entryFile);
				entryStream.close();
				
				unzipFiles.add(entryFile);
			}

			zfile.close();
			return unzipFiles;
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while unzipping specified file: " + zipFile, ex);
		}
	}

	/**
	 * Unzips the specified zip file to specified root folder.
	 * @param zipFile file to unzip file.
	 * @param rootFolder folder to which unzip should be done.
	 * @return Retuns the list of files unzipped.
	 */
	public static List<File> unzip(InputStream zipFile, File rootFolder)
	{
		try
		{
			ZipInputStream zis = new ZipInputStream(zipFile);
			
			ZipEntry zipEntry = null;
			List<File> unzipFiles = new LinkedList<File>();
			
			while( (zipEntry = zis.getNextEntry()) != null)
			{
				System.out.println("Unzipping: " + zipEntry.getName());
				File entryFile = new File(rootFolder, zipEntry.getName().replace("/", File.separator));
				
				if(zipEntry.isDirectory())
				{
					FileUtils.forceMkdir(entryFile);
					continue;
				}
				
				FileUtils.forceMkdir(entryFile.getParentFile());
				
				FileUtils.copyInputStreamToFile(new BufferedInputStream(zis) 
				{
					public void close()
					{
						//dont do anything here, as zip stream should not be closed yet
					}
				}, entryFile);

				unzipFiles.add(entryFile);
			}

			return unzipFiles;
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while unzipping specified file: " + zipFile, ex);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		zipDirectory(new File("C:\\Office\\Stories\\SDP"), new File("C:\\temp\\sms.zip"));
		System.out.println("Zip is completed");
		unzip(new File("C:\\temp\\sms.zip"), new File("c:\\temp\\sms"));
		System.out.println("Unzip is done..");
	}
}
