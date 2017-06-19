package com.yukthitech.autox;

import java.util.Arrays;
import java.util.HashSet;
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
}
