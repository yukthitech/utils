package com.yukthitech.autox.ide.exeenv;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.monitor.MonitorServer;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Manager to manage current environments.
 * @author akiran
 */
@Service
public class ExecutionEnvironmentManager
{
	private static Logger logger = LogManager.getLogger(ExecutionEnvironmentManager.class);
	
	@Autowired
	private IdeContext ideContext;
	
	/**
	 * Fetches next available socket.
	 * @return next available socket.
	 */
	private synchronized int fetchNextAvailablePort()
	{
		try
		{
			ServerSocket testSocket = new ServerSocket(0);
			int nextPort = testSocket.getLocalPort();
			testSocket.close();
			
			return nextPort;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An errror occurred while fetching available socket", ex);
		}
	}
	
	private ExecutionEnvironment startAutoxEnvironment(String envName, Project project, String... extraArgs)
	{
		String classpath = System.getProperty("java.class.path");
		String javaCmd = "java";
		String outputDir = "autox-report";
		
		int monitorPort = fetchNextAvailablePort();
		
		List<String> command = new ArrayList<>( Arrays.asList(
			javaCmd, 
			"-classpath", classpath,
			"-D" + MonitorServer.SYS_PROP_MONITOR_PORT + "=" + monitorPort,
			AutomationLauncher.class.getName(),
			project.getAppConfigFilePath(),
			"-rf", new File(project.getBaseFolderPath(), outputDir).getPath(),
			"--report-opening-disabled", "true"
		) );
		
		command.addAll(Arrays.asList(extraArgs));
		
		
		logger.debug("Executing command: {}", command.stream().collect(Collectors.joining(" ")));
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory( new File(project.getBaseFolderPath()) );
		
		try
		{
			ExecutionEnvironment env = new ExecutionEnvironment(envName, builder.start(), ideContext.getProxy(), monitorPort);
			ideContext.getProxy().newEnvironmentStarted(env);
			
			return env;
		}catch(IOException ex)
		{
			throw new InvalidStateException("An error occurred while starting autox process", ex);
		}
	}
	
	public ExecutionEnvironment executeTestSuite(Project project, String testSuite)
	{
		return startAutoxEnvironment("ts-" + testSuite, project, "-ts", testSuite);
	}
	
	public ExecutionEnvironment executeTestCase(Project project, String testCase)
	{
		return startAutoxEnvironment("tc-" + testCase, project, "-tc", testCase);
	}
}
