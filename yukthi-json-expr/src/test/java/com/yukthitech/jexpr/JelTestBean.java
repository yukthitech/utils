/*
 * Copyright (c) 2006,2007 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc.
 * Use is subject to license terms.
 */
package com.yukthitech.jexpr;

/**
 * Test data for jel testing.
 * @author akiran
 */
public class JelTestBean
{
	/**
	 * Name of test case.
	 */
	private String name;
	
	/**
	 * Context to be used.
	 */
	private String context;
	
	/**
	 * Template to be used.
	 */
	private String template;
	
	/**
	 * Expected result.
	 */
	private String expectedResult;
	
	/**
	 * Expected error.
	 */
	private String expectedError;

	/**
	 * Gets the context to be used.
	 *
	 * @return the context to be used
	 */
	public String getContext()
	{
		return context;
	}

	/**
	 * Sets the context to be used.
	 *
	 * @param context the new context to be used
	 */
	public void setContext(String context)
	{
		this.context = context;
	}

	/**
	 * Gets the template to be used.
	 *
	 * @return the template to be used
	 */
	public String getTemplate()
	{
		return template;
	}

	/**
	 * Sets the template to be used.
	 *
	 * @param template the new template to be used
	 */
	public void setTemplate(String template)
	{
		this.template = template;
	}

	/**
	 * Gets the expected result.
	 *
	 * @return the expected result
	 */
	public String getExpectedResult()
	{
		return expectedResult;
	}

	/**
	 * Sets the expected result.
	 *
	 * @param expectedResult the new expected result
	 */
	public void setExpectedResult(String expectedResult)
	{
		this.expectedResult = expectedResult;
	}
	
	/**
	 * Gets the name of test case.
	 *
	 * @return the name of test case
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of test case.
	 *
	 * @param name the new name of test case
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Gets the expected error.
	 *
	 * @return the expected error
	 */
	public String getExpectedError()
	{
		return expectedError;
	}

	/**
	 * Sets the expected error.
	 *
	 * @param expectedError the new expected error
	 */
	public void setExpectedError(String expectedError)
	{
		this.expectedError = expectedError;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString()
	{
		return name;
	}
}
