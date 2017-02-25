package com.yukthitech.automation;

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
	
}
