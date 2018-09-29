package com.yukthitech.autox.ide.exeenv;

import java.io.File;
import java.io.IOException;
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
	
	private ExecutionEnvironment startAutoxEnvironment(String envName, Project project, String... extraArgs)
	{
		String classpath = System.getProperty("java.class.path");
		String javaCmd = "java";
		String outputDir = "autox-report";
		
		List<String> command = new ArrayList<>( Arrays.asList(
			javaCmd, "-classpath", classpath, AutomationLauncher.class.getName(),
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
			ExecutionEnvironment env = new ExecutionEnvironment(envName, builder.start(), ideContext.getProxy());
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
