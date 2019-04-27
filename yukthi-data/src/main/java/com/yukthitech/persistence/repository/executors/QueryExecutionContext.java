/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthitech.persistence.repository.executors;

import com.yukthitech.persistence.conversion.ConversionService;

/**
 * Encapsulation of parameters and other context sent during query executor execution.
 * @author akiran
 */
public class QueryExecutionContext
{
	/**
	 * Conversion service to be used by query executors.
	 */
	private ConversionService conversionService;
	
	/**
	 * Repository execution context.
	 */
	private Object repositoryExecutionContext;
	
	public QueryExecutionContext(ConversionService conversionService)
	{
		this.conversionService = conversionService;
	}
	
	public ConversionService getConversionService()
	{
		return conversionService;
	}
	
	public void setRepositoryExecutionContext(Object repositoryExecutionContext)
	{
		this.repositoryExecutionContext = repositoryExecutionContext;
	}
	
	public Object getRepositoryExecutionContext()
	{
		return repositoryExecutionContext;
	}
}
