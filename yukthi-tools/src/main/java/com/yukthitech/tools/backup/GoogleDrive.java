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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDrive
{
	private static Logger logger = LogManager.getLogger(GoogleDrive.class);
	
	/** 
	 * Application name. 
	 */
	private static final String APPLICATION_NAME = "Yukthi-Google-Drive";

	/** 
	 * Directory to store user credentials for this application. 
	 */
	private static final java.io.File DATA_STORE_DIR = new java.io.File("./credentials/drive-java-quickstart");

	/** 
	 * Global instance of the {@link FileDataStoreFactory}. 
	 */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** 
	 * Global instance of the JSON factory. 
	 */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	/** 
	 * Global instance of the HTTP transport. 
	 */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/drive-java-quickstart
	 */
	private static final List<String> SCOPES = new ArrayList<>( DriveScopes.all() );

	static
	{
		try
		{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch(Throwable t)
		{
			t.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Drive cred object.
	 */
	private Credential credential;

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public Credential authorize() throws IOException
	{
		// Load client secrets.
		InputStream in = new FileInputStream("./client_secret.json");
		
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		in.close();

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	private Drive getDriveService() throws IOException
	{
		if(credential == null)
		{
			credential = authorize();
		}
		
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}
	
	public void backup(List<BackupConfig.Entry> pathLst) throws Exception
	{
		Drive drive = getDriveService();
		Files driveFiles = drive.files();
		
		//fetch current files from drive
		FileList result = driveFiles.list().setPageSize(100).setFields("nextPageToken, files(id, name)").execute();
		List<File> files = result.getFiles();
		Map<String, String> nameToFileId = new HashMap<>();
		
		if(files != null)
		{
			for(File file : files)
			{
				nameToFileId.put(file.getName(), file.getId());
			}
		}
		
		//loop through files to backup
		for(BackupConfig.Entry entry : pathLst)
		{
			java.io.File path = new java.io.File(entry.getPath());
			
			if(!path.exists() || !path.isFile())
			{
				logger.warn("Specified file does not exist or not a file. So ignoring taking backup. File: " + path.getAbsolutePath());
				continue;
			}
			
			if(nameToFileId.containsKey(entry.getName()))
			{
				String backupName = entry.getName() + "_bkup";
				
				logger.debug("Found file '{}' to be already existing on drive. This would be renamed with name: {}", entry.getName(), backupName);
				
				if(nameToFileId.containsKey(backupName))
				{
					logger.debug("For file '{}' found backup file to be already existing on drive. This will be deleted now.", entry.getName());
					
					driveFiles.delete(nameToFileId.get(backupName)).execute();
				}
				
				File backupFile = new File();
				backupFile.setName(backupName);
			
				//take backup of current file
				driveFiles.copy(nameToFileId.get(entry.getName()), backupFile).execute();
				
				//delete current file
				driveFiles.delete(nameToFileId.get(entry.getName())).execute();
			}

			logger.debug("Backing up file: {}", entry.getPath());
			
			File newFile = new File();
			newFile.setName(entry.getName());
			
			FileContent fileContent = new FileContent(null, path);
			
			driveFiles.create(newFile, fileContent)
				.setFields("id")
				.execute();
		}
	}
}
