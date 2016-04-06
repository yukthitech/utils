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

package com.yukthi.utils.annotations;

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
