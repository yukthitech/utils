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

import java.util.Date;

import com.yukthitech.validation.annotations.FutureOrToday;
import com.yukthitech.validation.annotations.GreaterThanEquals;

/**
 * @author akiran
 *
 */
@Model
public class GEBean
{
	@GreaterThanEquals(field = "field2")
	private Integer field1;

	private Integer field2;
	
	@FutureOrToday
	@GreaterThanEquals(field = "field4")
	private Date field3;
	
	private Date field4;

	public GEBean(int field1, int field2)
	{
		this.field1 = field1;
		this.field2 = field2;
	}
	
	public GEBean(Date field3, Date field4)
	{
		this.field3 = field3;
		this.field4 = field4;
	}


	/**
	 * @return the {@link #field1 field1}
	 */
	public Integer getField1()
	{
		return field1;
	}

	/**
	 * @param field1 the {@link #field1 field1} to set
	 */
	public void setField1(Integer field1)
	{
		this.field1 = field1;
	}

	/**
	 * @return the {@link #field2 field2}
	 */
	public Integer getField2()
	{
		return field2;
	}

	/**
	 * @param field2 the {@link #field2 field2} to set
	 */
	public void setField2(Integer field2)
	{
		this.field2 = field2;
	}

	/**
	 * @return the {@link #field3 field3}
	 */
	public Date getField3()
	{
		return field3;
	}

	/**
	 * @param field3 the {@link #field3 field3} to set
	 */
	public void setField3(Date field3)
	{
		this.field3 = field3;
	}

	/**
	 * @return the {@link #field4 field4}
	 */
	public Date getField4()
	{
		return field4;
	}

	/**
	 * @param field4 the {@link #field4 field4} to set
	 */
	public void setField4(Date field4)
	{
		this.field4 = field4;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Field1: ").append(field1);
		builder.append(",").append("Field2: ").append(field2);
		builder.append(",\n").append("Field3: ").append(field3);
		builder.append(",").append("Field4: ").append(field4);

		builder.append("]");
		return builder.toString();
	}


}
