package com.yukthitech.tools.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtpUploader
{
	private static Logger logger = LogManager.getLogger(FtpUploader.class);

	private static Pattern URL_PATTERN = Pattern.compile("([\\w\\-\\.]+)\\:(\\d+)\\@(\\w+)\\/(.+)");

	private static FTPClient connect(boolean secured, String host, int port, String user, String password) throws Exception
	{
		FTPClient ftp = secured ? new FTPSClient() : new FTPClient();
		ftp.enterLocalActiveMode();
		ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		ftp.connect(host, port);
		int reply = ftp.getReplyCode();
		
		logger.debug("Post Connection got reply code as: " + reply);
		
		if(!FTPReply.isPositiveCompletion(reply))
		{
			ftp.disconnect();
			throw new IOException("Exception in connecting to FTP Server");
		}

		if(!ftp.login(user, password))
		{
			ftp.disconnect();
			System.err.println("Authentication failed!!!");
			System.exit(-1);
		}
		
		return ftp;
	}

	public static void main(String[] args) throws Exception
	{
		if(args.length < 3)
		{
			System.err.println("Wrong number of arguments specified.");
			System.err.println("Syntax: java " + FtpUploader.class.getName() + " <host:port@user/password> <remote-folder-path> <local-file1> [local-file2 ...]");
			System.exit(-1);
		}
		
		String ftpUrl = args[0];
		String remotePath = args[1];
		
		if(!remotePath.endsWith("/"))
		{
			remotePath = remotePath + "/";
		}
		
		List<File> localFiles = new ArrayList<File>();
		
		for(int i = 2; i < args.length; i++)
		{
			File file = new File(args[i]);
			
			if(!file.exists())
			{
				System.err.println("Specified local file does not exist: " + file);
				System.exit(-1);
			}
			
			localFiles.add(file);
		}

		Matcher matcher = URL_PATTERN.matcher(ftpUrl);

		if(!matcher.matches())
		{
			System.err.println("Invalid ftp url specified: " + ftpUrl);
			System.exit(-1);
		}

		String host = matcher.group(1);
		int port = Integer.parseInt(matcher.group(2));
		String user = matcher.group(3);
		String password = matcher.group(4);

		logger.debug("Connecting to [Host: {}, Port: {}, User: {}]", host, port, user);

		FTPClient ftpClient = connect("true".equalsIgnoreCase(System.getProperty("secured")), host, port, user, password);
		
		for(File file : localFiles)
		{
			logger.debug("Uploading file: " + file.getPath());
			
			InputStream is = new FileInputStream(file);
			ftpClient.storeFile(remotePath + file.getName(), is);
			is.close();
		}
		
		ftpClient.disconnect();
	}
}
