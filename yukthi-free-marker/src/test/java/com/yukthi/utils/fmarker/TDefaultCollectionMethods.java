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
package com.yukthi.utils.fmarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.DefaultCollectionMethods.Group;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class TDefaultCollectionMethods
{
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	private Object eval(String expr, Object... args)
	{
		Map<String, Object> context = null;
		
		if(args.length > 1)
		{
			context = CommonUtils.toMap(args);
		}
		else
		{
			context = Collections.emptyMap();
		}
		
		return freeMarkerEngine.fetchValue("test", expr, context);
	}
	
	@Test
	public void testGroupBy()
	{
		List<TestBean> testBeans = Arrays.asList(
			new TestBean("test1"),
			new TestBean("test3"),
			new TestBean("test2"),
			new TestBean("test1"),
			new TestBean("test1"),
			new TestBean("test2")
		);
		
		List<Group> expectedRes = Arrays.asList(
			new Group("test1", Arrays.asList(new TestBean("test1"), new TestBean("test1"), new TestBean("test1"))),
			new Group("test3", Arrays.asList(new TestBean("test3"))),
			new Group("test2", Arrays.asList(new TestBean("test2"), new TestBean("test2")))
		);
		
		Object actRes = eval("groupBy(beans, 'name')", "beans", testBeans);
		System.out.println("Result: " + actRes);
		
		Assert.assertEquals(actRes, expectedRes);
	}

	@Test
	public void testSortBy()
	{
		List<TestBean> testBeans = Arrays.asList(
			new TestBean("test1"),
			new TestBean("test3"),
			new TestBean("test2"),
			new TestBean("test1"),
			new TestBean("test4"),
			new TestBean("test1"),
			new TestBean("test4"),
			new TestBean("test2")
		);
		
		List<TestBean> expectedRes = Arrays.asList(
				new TestBean("test1"), 
				new TestBean("test1"), 
				new TestBean("test1"),
				new TestBean("test2"),
				new TestBean("test2"),
				new TestBean("test3"),
				new TestBean("test4"),
				new TestBean("test4")
		);
		
		Object actRes = eval("sortBy(beans, 'name')", "beans", testBeans);
		System.out.println("Result: " + actRes);
		
		Assert.assertEquals(actRes, expectedRes);
	}

}
