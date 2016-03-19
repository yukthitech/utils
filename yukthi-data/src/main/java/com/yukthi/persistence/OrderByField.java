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

package com.yukthi.persistence;

import com.yukthi.persistence.repository.annotations.OrderByType;

/**
 * Order by clause field details.
 * @author akiran
 */
public class OrderByField
{
	/**
	 * Name of the order field.
	 */
	private String name;
	
	/**
	 * Order by type.
	 */
	private OrderByType orderByType;

	/**
	 * Instantiates a new order field.
	 *
	 * @param name the name
	 * @param orderByType the order by type
	 */
	public OrderByField(String name, OrderByType orderByType)
	{
		this.name = name;
		this.orderByType = orderByType;
	}

	/**
	 * Gets the name of the order field.
	 *
	 * @return the name of the order field
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the order field.
	 *
	 * @param name the new name of the order field
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the order by type.
	 *
	 * @return the order by type
	 */
	public OrderByType getOrderByType()
	{
		return orderByType;
	}

	/**
	 * Sets the order by type.
	 *
	 * @param orderByType the new order by type
	 */
	public void setOrderByType(OrderByType orderByType)
	{
		this.orderByType = orderByType;
	}
}
