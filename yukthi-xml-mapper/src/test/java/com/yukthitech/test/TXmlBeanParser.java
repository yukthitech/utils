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

package com.yukthitech.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.DynamicBeanParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.test.beans.DynamicTestBean;
import com.yukthitech.test.beans.ListBean;
import com.yukthitech.test.beans.MapBean;
import com.yukthitech.test.beans.TestDataBean;
import com.yukthitech.utils.CommonUtils;

/**
 * @author akiran
 *
 */
public class TXmlBeanParser
{
	@Test
	public void testDynamicBean()
	{
		DynamicTestBean bean = new DynamicTestBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean.xml"), bean);
		
		Assert.assertEquals(bean.getData().get("attr1"), "val1");
		Assert.assertEquals(bean.getData().get("attr2"), "val2");
		Assert.assertEquals(bean.getData().get("node1"), "node-val1");
		Assert.assertEquals(bean.getData().get("node2"), "node-val2");
		
		TestDataBean testDataBean = (TestDataBean)bean.getData().get("node3");
		Assert.assertEquals(testDataBean.getIntVal(), 100);
	}
	
	/**
	 * Ensure xml to dynamic bean converstion is happening properly. And also ensures dynamic bean to map-of-map 
	 * coversion is also happening properly.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testWithDynamicHandler()
	{
		DynamicBean bean = (DynamicBean) XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean-1.xml"), null, new DynamicBeanParserHandler());

		System.out.println(bean.toSimpleMap());
		
		Assert.assertEquals(bean.get("attr1"), "val1");
		Assert.assertEquals(bean.get("attr2"), "val2");
		
		Assert.assertEquals(bean.get("node1"), "node-val1");
		Assert.assertEquals(bean.get("node2"), Arrays.asList("node-val2", "node-val3"));
		
		Assert.assertTrue(bean.get("node3") instanceof DynamicBean);
		Assert.assertEquals( ((DynamicBean) bean.get("node3")).get("intVal"), "100");
		
		Assert.assertTrue(bean.get("node4") instanceof List);
		Assert.assertTrue( ((List) bean.get("node4")).get(0) instanceof DynamicBean);
		
		//Ensure conversion of dynamic bean to map is working fine
		Map<String, Object> map = bean.toSimpleMap();
		
		Assert.assertEquals(map.get("attr1"), "val1");
		Assert.assertEquals(map.get("attr2"), "val2");
		
		Assert.assertEquals(map.get("node1"), "node-val1");
		Assert.assertEquals(map.get("node2"), Arrays.asList("node-val2", "node-val3"));
		
		Assert.assertTrue(map.get("node3") instanceof Map);
		Assert.assertEquals( ((Map) map.get("node3")).get("intVal"), "100");
		
		Assert.assertTrue(bean.get("node4") instanceof List);
		Assert.assertTrue( ((List) map.get("node4")).get(0) instanceof Map);
	}

	/**
	 * Tests collection loading from xml.
	 */
	@Test
	public void testCollectionLoading()
	{
		ListBean bean = new ListBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/list-data.xml"), bean);
		
		Assert.assertEquals(bean.getStrList(), Arrays.asList("Value1", "Value2"));
		Assert.assertEquals(bean.getIntSet(), CommonUtils.toSet(10, 20));
		Assert.assertEquals(bean.getBeanList(), Arrays.asList(new TestDataBean(5), new TestDataBean(6)));
		Assert.assertEquals(bean.getObjectList(), Arrays.asList(new TestDataBean(5)));
	}
	
	@Test
	public void testMapLoading()
	{
		MapBean bean = new MapBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/map-data.xml"), bean);
		
		Assert.assertEquals(bean.getStrMap(), CommonUtils.toMap("key1", "Value1", "key2", "Value2"));
		Assert.assertEquals(bean.getIntMap(), CommonUtils.toMap(1, 10, 2, 20));
		Assert.assertEquals(bean.getBeanMap(), CommonUtils.toMap("key1", new TestDataBean(5), "key2", new TestDataBean(6)));
		Assert.assertEquals(bean.getObjectMap(), CommonUtils.toMap("key1", new TestDataBean(5)));
	}
}
