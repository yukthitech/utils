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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.ccg.xml.XMLBeanParser;


public class BackupManager
{
	private static Logger logger = LogManager.getLogger(BackupManager.class);
	
	/**
	 * Backup configuration.
	 */
	private BackupConfig configuration;
	
	/**
	 * Drives where backups will be taken.
	 */
	private GoogleDrive googleDrive = new GoogleDrive();
	
	public BackupManager(BackupConfig config)
	{
		this.configuration = config;
		
		Thread backupThread = new Thread()
		{
			public void run()
			{
				while(true)
				{
					takeBackups();
					
					//sleep till next backup time.
					try
					{
						logger.debug("Sleeping for {} Mins (next backup time)", configuration.getDurationMins());
						
						Thread.sleep(configuration.getDurationMins() * 60000);
					}catch(Exception ex)
					{
						//ignore
					}
				}
			}
		};
		
		backupThread.start();
	}
	
	/**
	 * Executes the pre-execute command specified.
	 * @param command
	 */
	private void executePrecommand(String command)
	{
		if(command == null || command.trim().length() == 0)
		{
			logger.debug("As not precommand is configured, skipping precommand execution.");
			return;
		}
		
		logger.debug("Executing preexecute command: {}", command);
		
		ProcessBuilder procBuilder = new ProcessBuilder(command.split("\\s+"));
		procBuilder.redirectErrorStream(true);
		
		try
		{
			Process process = procBuilder.start();
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));
			String line = null;
			StringBuilder output = new StringBuilder();
			
			while( (line = reader.readLine()) != null )
			{
				output.append("\t\t").append(line).append("\n");
			}
			
			is.close();
			
			logger.debug("Output of preexecute command was: \n{}", output);
		}catch(Exception ex)
		{
			logger.error("An error occurred while executing pre-execute command: " + command, ex);
			throw new IllegalStateException("An error occurred while executing pre-execute command: " + command, ex);
		}
	}
	
	/**
	 * Takes backup of specified files.
	 */
	private void takeBackups()
	{
		try
		{
			executePrecommand(configuration.getPreExecuteCommand());
			googleDrive.backup(configuration.getFiles());
		}catch(Exception ex)
		{
			logger.error("An error occurred while taking files backup", ex);
			throw new IllegalStateException("An error occurred while taking files backup", ex);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		File configFile = new File("./backup-config.xml");
		
		FileInputStream fis = new FileInputStream(configFile);
		BackupConfig config = new BackupConfig();
		
		XMLBeanParser.parse(fis, config);
		fis.close();

		//start backup manager
		new BackupManager(config);
	}
}
