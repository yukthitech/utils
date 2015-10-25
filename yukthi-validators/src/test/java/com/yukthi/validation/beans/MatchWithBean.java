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

import java.util.Date;

import com.yukthi.validation.annotations.MatchWith;

/**
 * @author akiran
 *
 */
@Model
public class MatchWithBean
{
	@MatchWith(field = "field2")
	private Integer field1;

	private Integer field2;
	
	@MatchWith(field = "field4")
	private Date field3;
	
	private Date field4;

	public MatchWithBean(Integer field1, Integer field2, Date field3, Date field4)
	{
		this.field1 = field1;
		this.field2 = field2;
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
