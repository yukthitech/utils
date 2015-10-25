/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.yukthi.validation.beans;

import com.yukthi.validation.annotations.Required;

/**
 * @author akiran
 *
 */
@Model
public class RequiredBean
{
	@Required
	private String stringField = "2334";

	@Required
	private int intField = 34;

	public RequiredBean(String stringField, int intField)
	{
		this.stringField = stringField;
		this.intField = intField;
	}

	/**
	 * @return the {@link #stringField stringField}
	 */
	public String getStringField()
	{
		return stringField;
	}

	/**
	 * @param stringField
	 *            the {@link #stringField stringField} to set
	 */
	public void setStringField(String stringField)
	{
		this.stringField = stringField;
	}

	/**
	 * @return the {@link #intField intField}
	 */
	public int getIntField()
	{
		return intField;
	}

	/**
	 * @param intField
	 *            the {@link #intField intField} to set
	 */
	public void setIntField(int intField)
	{
		this.intField = intField;
	}

}
