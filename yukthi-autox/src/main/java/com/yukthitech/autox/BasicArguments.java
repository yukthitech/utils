package com.yukthitech.autox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.cli.CliArgument;

/**
 * Command line mapper bean to accept basic extended arguments.
 * @author akiran
 */
public class BasicArguments
{
	/**
	 * If specified the execution will be limited only for this test suites.
	 */
	@CliArgument(name = "ts", longName = "test-suites", description = "Test suites to be executed (comma separated)", required = false)
	private String testSuites;
	
	/**
	 * If specified, test cases only with specified names will be executed. Users has to ensure dependency test cases, if any, are also included.
	 */
	@CliArgument(name = "tc", longName = "test-cases", description = "Test cases to be executed (comma separated). All dependency test cases also should be included.", required = false)
	private String testCases;
	
	/**
	 * Used to specify application properties which can then be injected into config files using {{}} expressions.
	 */
	@CliArgument(name = "prop", longName = "property-file", description = "Used to specify application properties which can then be injected into config files using #{} expressions", required = false)
	private String propertiesFile;

	/**
	 * Folder in which reports should be generated.
	 */
	@CliArgument(name = "rf", longName = "reports-folder", description = "Folder in which reports should be generated.", required = true)
	private String reportsFolder;
	
	/**
	 * Listener to be used for automation.
	 */
	@CliArgument(name = "list", longName = "automation-listener", description = "Automation listener to be configured.", required = false)
	private String automationListener;
	
	/**
	 * Flag to indicate report opening.
	 */
	@CliArgument(name = "rod", longName = "report-opening-disabled", description = "Boolean value to disable report opening at end. Which is not desired in CI/CD.", required = false)
	private boolean reportOpeningDisalbed;

	/**
	 * Used by ide. Flag used to indicate an interactive environment has to be started.
	 */
	@CliArgument(name = "ienv", longName = "interactive-environment", description = "Used by ide. Flag used to indicate an interactive environment has to be started.", required = false)
	private boolean interactiveEnvironment = false;

	/**
	 * Used only in interactive environment, indicates if global setup should be executed during start.
	 */
	@CliArgument(name = "ieglobal", longName = "interactive-execution-global", description = "Used only in interactive environment, indicates if global setup should be executed during start.", required = false)
	private boolean interactiveExecuteGlobal = false;

	/**
	 * Comma separated folder paths to which execution should be limited.
	 */
	@CliArgument(name = "flmt", longName = "folder-limits", description = "Comma separated folder paths to which execution should be limited.", required = false)
	private String folderLimits;
	
	/**
	 * Gets the if specified the execution will be limited only for this test suites.
	 *
	 * @return the if specified the execution will be limited only for this test suites
	 */
	public String getTestSuites()
	{
		return testSuites;
	}

	/**
	 * Sets the if specified the execution will be limited only for this test suites.
	 *
	 * @param testSuites the new if specified the execution will be limited only for this test suites
	 */
	public void setTestSuites(String testSuites)
	{
		this.testSuites = testSuites;
	}
	
	/**
	 * Fetches test suite names in the form of set.
	 * @return test suites to be executed, if specified. Otherwise null.
	 */
	public Set<String> getTestSuitesSet()
	{
		if(StringUtils.isBlank(testSuites))
		{
			return null;
		}
		
		Set<String> suites = new HashSet<>( Arrays.asList(testSuites.trim().split("\\s*\\,\\s*")) );
		return suites;
	}

	/**
	 * Gets the if specified, test cases only with specified names will be executed. Users has to ensure dependency test cases, if any, are also included.
	 *
	 * @return the if specified, test cases only with specified names will be executed
	 */
	public String getTestCases()
	{
		return testCases;
	}

	/**
	 * Sets the if specified, test cases only with specified names will be executed. Users has to ensure dependency test cases, if any, are also included.
	 *
	 * @param testCases the new if specified, test cases only with specified names will be executed
	 */
	public void setTestCases(String testCases)
	{
		this.testCases = testCases;
	}
	
	/**
	 * Fetches test case names in the form of set.
	 * @return test cases to be executed, if specified. Otherwise null.
	 */
	public Set<String> getTestCasesSet()
	{
		if(StringUtils.isBlank(testCases))
		{
			return null;
		}
		
		Set<String> cases = new HashSet<>( Arrays.asList(testCases.trim().split("\\s*\\,\\s*")) );
		return cases;
	}

	/**
	 * Gets the used to specify application properties which can then be injected into config files using {{}} expressions.
	 *
	 * @return the used to specify application properties which can then be injected into config files using {{}} expressions
	 */
	public String getPropertiesFile()
	{
		return propertiesFile;
	}

	/**
	 * Sets the used to specify application properties which can then be injected into config files using {{}} expressions.
	 *
	 * @param properties the new used to specify application properties which can then be injected into config files using {{}} expressions
	 */
	public void setPropertiesFile(String properties)
	{
		this.propertiesFile = properties;
	}

	/**
	 * Gets the folder in which reports should be generated.
	 *
	 * @return the folder in which reports should be generated
	 */
	public String getReportsFolder()
	{
		return reportsFolder;
	}

	/**
	 * Sets the folder in which reports should be generated.
	 *
	 * @param reportsFolder the new folder in which reports should be generated
	 */
	public void setReportsFolder(String reportsFolder)
	{
		this.reportsFolder = reportsFolder;
	}

	/**
	 * Gets the listener to be used for automation.
	 *
	 * @return the listener to be used for automation
	 */
	public String getAutomationListener()
	{
		return automationListener;
	}

	/**
	 * Sets the listener to be used for automation.
	 *
	 * @param automationListener the new listener to be used for automation
	 */
	public void setAutomationListener(String automationListener)
	{
		this.automationListener = automationListener;
	}

	/**
	 * Gets the flag to indicate report opening.
	 *
	 * @return the flag to indicate report opening
	 */
	public boolean isReportOpeningDisalbed()
	{
		return reportOpeningDisalbed;
	}

	/**
	 * Sets the flag to indicate report opening.
	 *
	 * @param reportOpeningDisalbed the new flag to indicate report opening
	 */
	public void setReportOpeningDisalbed(boolean reportOpeningDisalbed)
	{
		this.reportOpeningDisalbed = reportOpeningDisalbed;
	}

	/**
	 * Gets the used by ide. Flag used to indicate an interactive environment has to be started.
	 *
	 * @return the used by ide
	 */
	public boolean isInteractiveEnvironment()
	{
		return interactiveEnvironment;
	}

	/**
	 * Sets the used by ide. Flag used to indicate an interactive environment has to be started.
	 *
	 * @param interactiveEnvironment the new used by ide
	 */
	public void setInteractiveEnvironment(boolean interactiveEnvironment)
	{
		this.interactiveEnvironment = interactiveEnvironment;
	}

	/**
	 * Gets the used only in interactive environment, indicates if global setup should be executed during start.
	 *
	 * @return the used only in interactive environment, indicates if global setup should be executed during start
	 */
	public boolean isInteractiveExecuteGlobal()
	{
		return interactiveExecuteGlobal;
	}

	/**
	 * Sets the used only in interactive environment, indicates if global setup should be executed during start.
	 *
	 * @param interactiveExecuteGlobal the new used only in interactive environment, indicates if global setup should be executed during start
	 */
	public void setInteractiveExecuteGlobal(boolean interactiveExecuteGlobal)
	{
		this.interactiveExecuteGlobal = interactiveExecuteGlobal;
	}

	/**
	 * Gets the comma separated folder paths to which execution should be limited.
	 *
	 * @return the comma separated folder paths to which execution should be limited
	 */
	public String getFolderLimits()
	{
		return folderLimits;
	}

	/**
	 * Sets the comma separated folder paths to which execution should be limited.
	 *
	 * @param folderLimits the new comma separated folder paths to which execution should be limited
	 */
	public void setFolderLimits(String folderLimits)
	{
		this.folderLimits = folderLimits;
	}
	
	/**
	 * Fetches the folder limits as file objects.
	 * @return folder limits as files
	 */
	public List<File> getFolderLimitFiles()
	{
		if(StringUtils.isBlank(folderLimits))
		{
			return null;
		}
		
		String files[] = folderLimits.split("\\s*\\,\\s*");
		List<File> resList = new ArrayList<>();
		
		for(String path : files)
		{
			File file = new File(path);
			
			if(!file.exists() || !file.isDirectory())
			{
				System.err.println("Invalid limit folder specified: " + path);
				System.exit(-1);
			}
			
			resList.add(file);
		}
		
		return resList;
	}
}
