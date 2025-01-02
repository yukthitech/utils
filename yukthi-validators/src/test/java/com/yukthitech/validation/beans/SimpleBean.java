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
package com.yukthitech.validation.beans;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.yukthitech.validation.annotations.MaxLen;
import com.yukthitech.validation.annotations.MinLen;
import com.yukthitech.validation.annotations.Mispattern;
import com.yukthitech.validation.annotations.NotEmpty;
import com.yukthitech.validation.annotations.PastOrToday;

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

	@Mispattern(regexp = "[a-z]+\\d+")
	@Mispattern(regexp = "\\d+[a-z]+")
	@Mispattern(regexp = "\\d+")
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
