package com.yukthitech.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

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
		try
		{
			File tempFile = File.createTempFile("temp", ".zip");
			
			FileOutputStream fos = new FileOutputStream(tempFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			for(String fileName : files.keySet())
			{
				ZipEntry zipEntry = new ZipEntry(fileName);
				zos.putNextEntry(zipEntry);
				
				FileInputStream input = new FileInputStream(files.get(fileName));
				IOUtils.copy(input, zos);
				
				zos.closeEntry();
				input.close();
			}
			
			zos.close();
			fos.close();
			
			return tempFile;
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while zipping specified files", ex);
		}
	}
}
