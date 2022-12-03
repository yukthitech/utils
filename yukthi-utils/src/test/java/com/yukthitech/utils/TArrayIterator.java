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
package com.yukthitech.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.test.ITestGroups;

/**
 * Test cases for array iterator
 * @author akiran
 */
public class TArrayIterator
{
	/**
	 * Test the array iterator works with primitive array
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testForPrimitveArray()
	{
		int arr[] = {1, 3, 4, 6};
		
		Iterator<Object> it = new ArrayIterator(arr);
		int index = 0;
		
		while(it.hasNext())
		{
			Assert.assertEquals(it.next(), arr[index]);
			index++;
		}
		
		//ensure all the array elements are iterated
		Assert.assertEquals(index, arr.length);
	}

	/**
	 * Test array iterator works properly with object array
	 */
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void testForObjectArray()
	{
		String arr[] = {"val1", "val2", "val3"};
		
		Iterator<Object> it = new ArrayIterator(arr);
		int index = 0;
		
		while(it.hasNext())
		{
			Assert.assertEquals(it.next(), arr[index]);
			index++;
		}

		//ensure all the array elements are iterated
		Assert.assertEquals(index, arr.length);
	}

	/**
	 * Test the bounds test is in place
	 */
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = NoSuchElementException.class)
	public void testBounds()
	{
		int arr[] = {1, 3, 4, 6};
		
		Iterator<Object> it = new ArrayIterator(arr);
		
		//as there is no check for more-elements this should throw exception
		while(true)
		{
			it.next();
		}
		
	}
	
	/**
	 * Ensure remove on iterator throws exception
	 */
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = UnsupportedOperationException.class)
	public void testRemove()
	{
		int arr[] = {1, 3, 4, 6};
		
		Iterator<Object> it = new ArrayIterator(arr);
		it.next();
		
		//remove should not be supported by array iterator
		it.remove();
	}
	
	/**
	 * Ensure passing non-arrays throws exceptions 
	 */
	@Test(groups = ITestGroups.UNIT_TESTS, expectedExceptions = IllegalArgumentException.class)
	public void testForNonArray()
	{
		new ArrayIterator("Some object");
	}
}
