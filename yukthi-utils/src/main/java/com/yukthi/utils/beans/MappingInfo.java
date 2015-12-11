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

package com.yukthi.utils.beans;

/**
 * Information about custom mapping between beans
 * @author akiran
 */
class MappingInfo
{
	/**
	 * Other bean property from which mapping needs to be done
	 */
	private NestedProperty externalProperty;
	
	/**
	 * Current bean's property
	 */
	private NestedProperty localProperty;

	public MappingInfo(NestedProperty externalProperty, NestedProperty localProperty)
	{
		this.externalProperty = externalProperty;
		this.localProperty = localProperty;
	}

	/**
	 * Gets the other bean property from which mapping needs to be done.
	 *
	 * @return the other bean property from which mapping needs to be done
	 */
	public NestedProperty getExternalProperty()
	{
		return externalProperty;
	}

	/**
	 * Gets the current bean's property.
	 *
	 * @return the current bean's property
	 */
	public NestedProperty getLocalProperty()
	{
		return localProperty;
	}
}
