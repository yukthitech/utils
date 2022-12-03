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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.test.ITestGroups;

/**
 * Unit tests for object wrapper
 * @author akiran
 */
public class TObjectWrapper
{
	private void testObjectWrapper(ObjectWrapper<String> wrapper, String initialValue)
	{
		Assert.assertEquals(initialValue, wrapper.getValue());
		
		wrapper.setValue("Some Value");
		Assert.assertEquals("Some Value", wrapper.getValue());
	}
	
	@Test(groups = ITestGroups.UNIT_TESTS)
	public void test()
	{
		testObjectWrapper(new ObjectWrapper<String>(), null);
		testObjectWrapper(new ObjectWrapper<String>(null), null);
		testObjectWrapper(new ObjectWrapper<String>("123"), "123");
	}
}
