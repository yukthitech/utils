package com.yukthitech.automation.test;

/**
 * Test case execution status.
 * @author akiran
 */
public enum TestStatus
{
	/**
	 * Represents success state of test case.
	 */
	SUCCESSUFUL,
	
	/**
	 * Indicates test case failed.
	 */
	FAILED,
	
	/**
	 * Indicates test case is skipped.
	 */
	SKIPPED,
	
	/**
	 * Indicates an error occurred while test case execution.
	 */
	ERRORED;
}
