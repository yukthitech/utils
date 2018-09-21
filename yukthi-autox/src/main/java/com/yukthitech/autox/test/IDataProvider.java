package com.yukthitech.autox.test;

import java.util.List;

/**
 * Abstraction of data provider.
 * @author akiran
 */
public interface IDataProvider
{
	/**
	 * Name of the data provider, which in turn will be used to set data on context
	 * at time of test case execution.
	 * @return Name of the data provider.
	 */
	public String getName();
	
	/**
	 * Provides the list of step data using which test case needs to be repeated.
	 * @return list of step data.
	 */
	public List<TestCaseData> getStepData();
	
	/**
	 * If enabled, expressions will be parsed before test-case/step execution at data level.
	 * @return true if parsing should be avoided.
	 */
	public default boolean isParsingEnabled()
	{
		return false;
	}
}
