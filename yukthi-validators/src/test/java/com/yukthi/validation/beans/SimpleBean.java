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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.yukthi.validation.annotations.MaxLen;
import com.yukthi.validation.annotations.MinLen;
import com.yukthi.validation.annotations.Mispattern;
import com.yukthi.validation.annotations.NotEmpty;
import com.yukthi.validation.annotations.PastOrToday;

/**
 * @author akiran
 *
 */
public class SimpleBean
{
	@MaxLen(5)
	private String maxLenField;

	@MinLen(3)
	private String minLenField;

	@Mispattern(regexp = { "[a-z]+\\d+", "\\d+[a-z]+", "\\d+" })
	private String misspatternField;

	@NotEmpty
	private String notEmptyString = "fdf";

	@NotEmpty
	private List<String> notEmptyCollection = Arrays.asList("1", "2");

	@PastOrToday
	private Date pastOrToday;

	public SimpleBean(String maxLenField, String minLenField, String misspatternField)
	{
		this.maxLenField = maxLenField;
		this.minLenField = minLenField;
		this.misspatternField = misspatternField;
	}

	public SimpleBean(String notEmptyString, List<String> notEmptyCollection)
	{
		this.notEmptyString = notEmptyString;
		this.notEmptyCollection = notEmptyCollection;
	}

	public SimpleBean(Date pastOrToday)
	{
		this.pastOrToday = pastOrToday;
	}

	/**
	 * @return the {@link #maxLenField maxLenField}
	 */
	public String getMaxLenField()
	{
		return maxLenField;
	}

	/**
	 * @param maxLenField
	 *            the {@link #maxLenField maxLenField} to set
	 */
	public void setMaxLenField(String maxLenField)
	{
		this.maxLenField = maxLenField;
	}

	/**
	 * @return the {@link #minLenField minLenField}
	 */
	public String getMinLenField()
	{
		return minLenField;
	}

	/**
	 * @param minLenField
	 *            the {@link #minLenField minLenField} to set
	 */
	public void setMinLenField(String minLenField)
	{
		this.minLenField = minLenField;
	}

	/**
	 * @return the {@link #misspatternField misspatternField}
	 */
	public String getMisspatternField()
	{
		return misspatternField;
	}

	/**
	 * @param misspatternField
	 *            the {@link #misspatternField misspatternField} to set
	 */
	public void setMisspatternField(String misspatternField)
	{
		this.misspatternField = misspatternField;
	}

	/**
	 * @return the {@link #notEmptyString notEmptyString}
	 */
	public String getNotEmptyString()
	{
		return notEmptyString;
	}

	/**
	 * @param notEmptyString
	 *            the {@link #notEmptyString notEmptyString} to set
	 */
	public void setNotEmptyString(String notEmptyString)
	{
		this.notEmptyString = notEmptyString;
	}

	/**
	 * @return the {@link #notEmptyCollection notEmptyCollection}
	 */
	public List<String> getNotEmptyCollection()
	{
		return notEmptyCollection;
	}

	/**
	 * @param notEmptyCollection
	 *            the {@link #notEmptyCollection notEmptyCollection} to set
	 */
	public void setNotEmptyCollection(List<String> notEmptyCollection)
	{
		this.notEmptyCollection = notEmptyCollection;
	}

	/**
	 * @return the {@link #pastOrToday pastOrToday}
	 */
	public Date getPastOrToday()
	{
		return pastOrToday;
	}

	/**
	 * @param pastOrToday
	 *            the {@link #pastOrToday pastOrToday} to set
	 */
	public void setPastOrToday(Date pastOrToday)
	{
		this.pastOrToday = pastOrToday;
	}

}
