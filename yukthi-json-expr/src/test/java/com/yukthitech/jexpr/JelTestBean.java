/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 * Pojo context to be used.
	 */
	private LibraryContext pojoContext;
	
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
	
	public LibraryContext getPojoContext()
	{
		return pojoContext;
	}

	public void setPojoContext(LibraryContext pojoContext)
	{
		this.pojoContext = pojoContext;
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
