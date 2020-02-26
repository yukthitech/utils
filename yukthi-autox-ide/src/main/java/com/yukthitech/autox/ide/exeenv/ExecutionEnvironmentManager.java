package com.yukthitech.autox.ide.exeenv;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.AutomationLauncher;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.UiLayout;
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
	
	@Autowired
	private UiLayout uiLayout;
	
	/**
	 * Mapping from project to execution environment.
	 */
	private Map<String, ExecutionEnvironment> interactiveEnvironments = new HashMap<>();
	
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
		String classpath = project.getProjectClassLoader().toClassPath();
		String javaCmd = "java";
		String outputDir = "autox-report";
		File reportFolder = new File(project.getBaseFolderPath(), outputDir);
		
		int monitorPort = fetchNextAvailablePort();
		//Eg: -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
		String debugArg = System.getProperty("autox.debug.enabled");
		
		//if debug args are not specified, use dummy args
		if(StringUtils.isBlank(debugArg))
		{
			debugArg = "-Ddummy=true";
		}
		
		List<String> command = new ArrayList<>( Arrays.asList(
			javaCmd, 
			"-classpath", classpath,
			debugArg,
			"-D" + MonitorServer.SYS_PROP_MONITOR_PORT + "=" + monitorPort,
			AutomationLauncher.class.getName(),
			project.getAppConfigFilePath(),
			"-prop", project.getAppPropertyFilePath(), 
			"-rf", reportFolder.getPath(),
			"--report-opening-disabled", "true"
		) );
		
		command.addAll(Arrays.asList(extraArgs));
		
		StringBuilder initMssg = new StringBuilder();
		initMssg.append(String.format("Executing command: %s", command.stream().collect(Collectors.joining(" "))));
		
		logger.debug(initMssg);
		initMssg.append("\n\n");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory( new File(project.getBaseFolderPath()) );
		
		//TODO: Temp workaround to avoid swap lines between out and err streams. Need proper fix.
		builder.redirectErrorStream(true);
		
		try
		{
			ExecutionEnvironment env = new ExecutionEnvironment(project, envName, builder.start(), ideContext.getProxy(), monitorPort, 
					reportFolder, initMssg.toString(), uiLayout);
			
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
	
	public ExecutionEnvironment executeTestSuiteFolder(Project project, List<File> testSuiteFolder)
	{
		String foldersPath = testSuiteFolder.stream()
			.map(file -> file.getPath())
			.collect(Collectors.joining(","));
		
		String firstFolderName = testSuiteFolder.get(0).getName();
		return startAutoxEnvironment("dir-" + firstFolderName, project, "-flmt", foldersPath);
	}

	public ExecutionEnvironment executeProject(Project project)
	{
		return startAutoxEnvironment(project.getName(), project);
	}

	public synchronized ExecutionEnvironment getInteractiveEnvironment(Project project)
	{
		ExecutionEnvironment env = interactiveEnvironments.get(project.getProjectFilePath());
		
		if(env != null && !env.isTerminated())
		{
			return env;
		}
		
		return null;
	}

	public synchronized ExecutionEnvironment startInteractiveEnvironment(Project project, boolean executeGlobalSetup)
	{
		ExecutionEnvironment env = interactiveEnvironments.get(project.getProjectFilePath());
		
		if(env != null && !env.isTerminated())
		{
			throw new InvalidStateException("For project '{}' interactive environment is already running", project.getName());
		}
		
		env = startAutoxEnvironment("*Interactive-" + project.getName(), project, 
				"--interactive-environment", "true", 
				"--interactive-execution-global", "" + executeGlobalSetup);
		
		env.setInteractive(true);
		interactiveEnvironments.put(project.getProjectFilePath(), env);
		
		return env;
	}
}
