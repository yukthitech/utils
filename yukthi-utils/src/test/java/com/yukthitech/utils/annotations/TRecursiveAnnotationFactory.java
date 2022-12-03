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
package com.yukthitech.utils.annotations;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author akiran
 *
 */
public class TRecursiveAnnotationFactory
{
	private Class<?> testClass = TestInterface.class;
	private RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	/**
	 * Tests when direct annotation is defined, tehs aame is returned
	 * @throws Exception
	 */
	@Test
	public void testDirectAnnotation() throws Exception
	{
		Method method = testClass.getMethod("directAnnotation");
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		
		Assert.assertEquals(searchResult.count(), 10);
		Assert.assertEquals(searchResult.mappings()[0].field(), "field1");
		Assert.assertEquals(searchResult.mappings()[0].property(), "property1");
		Assert.assertEquals(searchResult.returnMapping().field(), "retField1");
		Assert.assertEquals(searchResult.returnMapping().property(), "retProperty1");
	}
	
	/**
	 * Tests when annotation is defined annotation of target method
	 * @throws Exception
	 */
	@Test
	public void testRecursiveAnnotation() throws Exception
	{
		Method method = testClass.getMethod("recursiveAnnotaion");
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		
		Assert.assertEquals(searchResult.count(), 20);
		Assert.assertEquals(searchResult.mappings()[0].field(), "field2");
		Assert.assertEquals(searchResult.mappings()[0].property(), "property2");
		Assert.assertEquals(searchResult.returnMapping().field(), "retField2");
		Assert.assertEquals(searchResult.returnMapping().property(), "retProperty2");
	}
	
	/**
	 * Tests ability to override simple property
	 * @throws Exception
	 */
	@Test
	public void testSimplePropertyOverride() throws Exception
	{
		Method method = testClass.getMethod("simplePropOverride");
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		
		Assert.assertEquals(searchResult.count(), 30);
		Assert.assertEquals(searchResult.mappings()[0].field(), "field3");
		Assert.assertEquals(searchResult.mappings()[0].property(), "property3");
		Assert.assertEquals(searchResult.returnMapping().field(), "retField3");
		Assert.assertEquals(searchResult.returnMapping().property(), "retProperty3");
	}

	/**
	 * Tests ability to override nested property
	 * @throws Exception
	 */
	@Test
	public void testNestedPropertyOverride() throws Exception
	{
		Method method = testClass.getMethod("nestedPropPropOverride");
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		
		Assert.assertEquals(searchResult.count(), 40);
		Assert.assertEquals(searchResult.mappings()[0].field(), "field4");
		Assert.assertEquals(searchResult.mappings()[0].property(), "property4");
		Assert.assertEquals(searchResult.returnMapping().field(), "retField4");
		Assert.assertEquals(searchResult.returnMapping().property(), "retProperty4");
	}

	/**
	 * Tests ability to override array property (and other properties simultaneously)
	 * @throws Exception
	 */
	@Test
	public void testArrayPropertyOverride() throws Exception
	{
		Method method = testClass.getMethod("arrayPropOverride");
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		
		Assert.assertEquals(searchResult.count(), 50);
		Assert.assertEquals(searchResult.mappings()[0].field(), "field5");
		Assert.assertEquals(searchResult.mappings()[0].property(), "property5");
		Assert.assertEquals(searchResult.returnMapping().field(), "retField5");
		Assert.assertEquals(searchResult.returnMapping().property(), "retProperty5");
	}
	
	@Test
	public void testMultiOverride() throws Exception
	{
		Method method = testClass.getMethod("multiOverride");
		SearchResult searchResult = recursiveAnnotationFactory.findAnnotationRecursively(method, SearchResult.class);
		
		Assert.assertEquals(searchResult.count(), 60);
		Assert.assertEquals(searchResult.mappings()[0].field(), "field6");
		Assert.assertEquals(searchResult.mappings()[0].property(), "property6");
		Assert.assertEquals(searchResult.returnMapping().field(), "field6");
		Assert.assertEquals(searchResult.returnMapping().property(), "retProperty6");
		
		OrderBy orderBy = recursiveAnnotationFactory.findAnnotationRecursively(method, OrderBy.class);
		Assert.assertEquals(orderBy.fields()[0], "field6");
	}
	
	@Test
	public void testSuppression() throws Exception
	{
		Assert.assertNull(recursiveAnnotationFactory.findAllAnnotationsRecursively(testClass.getMethod("suppressMethod1"), SearchResult.class));
		Assert.assertNotNull(recursiveAnnotationFactory.findAllAnnotationsRecursively(testClass.getMethod("suppressMethod2"), SearchResult.class));
	}

	//TODO: add test cases for multi dimension array, sub annotation override
}
