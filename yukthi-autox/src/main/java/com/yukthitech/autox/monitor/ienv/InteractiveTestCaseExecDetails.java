package com.yukthitech.autox.monitor.ienv;

import java.io.Serializable;

/**
 * Details of test case execution in interactive environment.
 * @author akiran
 */
public class InteractiveTestCaseExecDetails implements Serializable
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Active test case to be executed.
	 */
	private String testCase;
	
	/**
	 * File in which execution should stop.
	 */
	private String filePath;
	
	/**
	 * Line number till which execution should happen.
	 */
	private int lineNumber;
	
	/**
	 * Instantiates a new interactive test case exec details.
	 */
	public InteractiveTestCaseExecDetails()
	{}
	
	/**
	 * Instantiates a new interactive test case exec details.
	 *
	 * @param testCase the test case
	 * @param lineNumber the line number
	 */
	public InteractiveTestCaseExecDetails(String testCase, String filePath, int lineNumber)
	{
		this.testCase = testCase;
		this.filePath = filePath;
		this.lineNumber = lineNumber;
	}

	/**
	 * Gets the active test case to be executed.
	 *
	 * @return the active test case to be executed
	 */
	public String getTestCase()
	{
		return testCase;
	}

	/**
	 * Sets the active test case to be executed.
	 *
	 * @param testCase the new active test case to be executed
	 */
	public void setTestCase(String testCase)
	{
		this.testCase = testCase;
	}

	/**
	 * Gets the line number till which execution should happen.
	 *
	 * @return the line number till which execution should happen
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * Sets the line number till which execution should happen.
	 *
	 * @param lineNumber the new line number till which execution should happen
	 */
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	/**
	 * Gets the file in which execution should stop.
	 *
	 * @return the file in which execution should stop
	 */
	public String getFilePath()
	{
		return filePath;
	}

	/**
	 * Sets the file in which execution should stop.
	 *
	 * @param filePath the new file in which execution should stop
	 */
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Test Case: ").append(testCase);
		builder.append(",").append("File: ").append(filePath);
		builder.append(",").append("Line: ").append(lineNumber);

		builder.append("]");
		return builder.toString();
	}

}
