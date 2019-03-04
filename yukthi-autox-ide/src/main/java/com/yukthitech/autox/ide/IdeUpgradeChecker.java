package com.yukthitech.autox.ide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class IdeUpgradeChecker
{
	private static Logger logger = LogManager.getLogger(IdeUpgradeChecker.class);
	
	private static Properties loadAppProp() throws Exception
	{
		Properties prop = new Properties();
		InputStream is = IdeUpgradeChecker.class.getResourceAsStream("/application.properties");
		prop.load(is);
		
		is.close();
		return prop;
	}
	
	private static String getLatestVersion(Properties prop) throws Exception
	{
		String vesionUrl = prop.getProperty("autox.ide.version.url");
		InputStream is = new URL(vesionUrl).openStream();
		String latestVersion = IOUtils.toString(is);
		
		is.close();
		return latestVersion;
	}
	
	private static String getLocalVersion() throws Exception
	{
		InputStream is = IdeUpgradeChecker.class.getResourceAsStream("/version/version.txt");
		String version = IOUtils.toString(is);
		is.close();
		
		return version;
	}
	
	private static File downloadLatestIde(Properties prop) throws Exception
	{
		String downloadUrl = prop.getProperty("autox.ide.download.url");
		InputStream is = new URL(downloadUrl).openStream();
		File tempFile = File.createTempFile("autox-ide-latest", ".zip");
		
		FileUtils.copyInputStreamToFile(is, tempFile);
		
		is.close();
		return tempFile;
	}
	
	public static void unzipLib(File zipFile, File newLibFolder)
	{
		try
		{
			//create fresh new lib folder
			FileUtils.deleteDirectory(newLibFolder);
			FileUtils.forceMkdir(newLibFolder);
			
			//start unzipping lib folder
			ZipFile zfile = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> it = zfile.entries();
			
			ZipEntry zipEntry = null;
			String entry = null;
			
			while(it.hasMoreElements())
			{
				zipEntry = it.nextElement();

				if(zipEntry.isDirectory())
				{
					continue;
				}
				
				entry = zipEntry.getName().toLowerCase();
				
				if(!entry.startsWith("lib/") || !entry.endsWith(".jar"))
				{
					continue;
				}
				
				File entryFile = new File(newLibFolder, zipEntry.getName().replace("/", File.separator));
				entryFile = new File(newLibFolder, entryFile.getName());
				
				InputStream entryStream = zfile.getInputStream(zipEntry);
				FileUtils.copyInputStreamToFile(entryStream, entryFile);
				entryStream.close();
			}

			zfile.close();
		} catch(IOException ex)
		{
			throw new InvalidStateException("An exception occurred while unzipping specified file: " + zipFile, ex);
		}
	}

	public static void main(String[] args) throws Exception
	{
		Properties prop = loadAppProp();
		logger.debug("Loaded app properties...");
		
		String latestVersion = getLatestVersion(prop);
		logger.debug("Got latest version as: {}", latestVersion);
		
		String localVersion = getLocalVersion();
		logger.debug("Got local version as: {}", localVersion);
		
		if(latestVersion.equals(localVersion))
		{
			logger.debug("Found the ide is upto date.");
			System.exit(0);
		}
		
		logger.debug("Found new updates available. Downloading latest ide zip file (this may take few mins...");
		File latestIde = downloadLatestIde(prop);
		logger.debug("Downloaded latest ide as file: {}", latestIde.getPath());
		
		unzipLib(latestIde, new File(".." + File.separator + "lib-new"));
		logger.debug("New ide lib folder is created successfully...");
		
		latestIde.delete();
	}
}
